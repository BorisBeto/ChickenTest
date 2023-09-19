package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.component.TemporalVariables;
import com.ChickenTest.demoChickenTest.dto.EggDto;
import com.ChickenTest.demoChickenTest.entity.*;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.ChickenTest.demoChickenTest.service.ITransaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ChickenTest.demoChickenTest.component.TemporalVariables.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EggService implements ITransaction {
    @Autowired
    private ChickenService chickenService;
    private static final Logger logger = Logger.getLogger(EggService.class);
    @Autowired
    IEggRepository eggRepository;
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    IFarmRepository farmRepository;
    @Autowired
    ObjectMapper mapper;

    double precioTotalVendido = 0.0;
    double precioTotalComprado = 0.0;
    int cantidadComprados = 0;
    int cantidadVendidos = 0;
    int excesoHuevos = 0;

    public List<EggDto> getDataTableEgg(){
        List<Egg> eggs = eggRepository.findAll();
        List<EggDto> eggDtos = new ArrayList<>();

        for (Egg egg : eggs){
            EggDto eggDto = mapper.convertValue(egg, EggDto.class);
            eggDtos.add(eggDto);
        }

        return eggDtos;
    }

    private void verifyCantidadPositiva(int cantidad){
        if (cantidad <= 0){
            logger.error("La cantidad ingresada debe ser Entero positivo. El valor ingresado fue: " + cantidad);
            throw new RuntimeException("La cantidad ingresada debe ser Entero positivo.");
        }
    }

    public void verifyStock(int cantidad, int stockActual, int limiteStock){
        if ((stockActual + cantidad) > limiteStock){
            logger.error("Supero el limite de Stock de Huevos. Cantidad: " + cantidad + " . Stock actual de Huevos: " + stockActual + " .Limite Stock de Huevos: " + limiteStock);
            throw new RuntimeException("Supero la capacidad Máxima de Huevos");
        }
    }

    public boolean isLimiteSotck(int cantidad, int stockActual, int limiteStock){
        return (stockActual + cantidad) > limiteStock;
    }

    private void verifyDineroDisponible(double dineroDisponible, double costoTotal){
        if (dineroDisponible < costoTotal){
            logger.error("Saldo insuficiente. Monto total: $" + costoTotal + ". Dinero disponible: $" + dineroDisponible);
            throw new RuntimeException("Dinero disponible insuficiente.");
        }
    }

    private void verifyStockForSell(int cantidad, int stockActual){
        if (stockActual <= 0){
            logger.error("Actualmente no contiene Huevos en su granja. Stock actual: " + stockActual);
            throw new RuntimeException("Actualmente no posee Huevos en su granja.");
        } else if (cantidad > stockActual) {
            logger.error("No fue posible realizar la venta. Cantidad a vender: " + cantidad + " .Stock actual: " + stockActual);
            throw new RuntimeException("No tiene suficientes huevos para vender.");
        }
    }

    public void getPrecioTotalComprado(List<Egg> listEggsBuy){
        for (Egg egg : listEggsBuy){
            precioTotalComprado += egg.getPrecioComprado();
        }
        cantidadComprados += listEggsBuy.size();

    }

    public void getPrecioTotalVendido(List<Egg> listEggsASell){
        for (Egg egg : listEggsASell){
            precioTotalVendido += egg.getPrecio();
        }

        cantidadVendidos += listEggsASell.size();
    }

    public void updatePrice(double newPriceForBuy, double newPriceForSell){
        List<Egg> updatePriceEggs = eggRepository.findAll();
        for (Egg egg : updatePriceEggs){
            Store.PRECIO_COMPRA_EGG = newPriceForBuy;
            Store.PRECIO_VENTA_EGG = newPriceForSell;

            //egg.setPrecioComprado(Store.PRECIO_COMPRA_EGG);
            egg.setPrecio(Store.PRECIO_VENTA_EGG);
            eggRepository.save(egg);
        }
    }

    @Override
    public void buy(Farm farm, int cantidad) {
        int cantidadEgg = farm.getCantHuevos();
        int limiteEgg = farm.getLimiteHuevos();

        verifyCantidadPositiva(cantidad);
        verifyStock(cantidad, cantidadEgg, limiteEgg);
        verifyDineroDisponible(farm.getDinero(), cantidad * Store.PRECIO_COMPRA_EGG);

        List<Egg> listEggsBuy = new ArrayList<>();
        /*  Comprando Huevos.   */
        for (int i = 0; i < cantidad; i++){
            Egg egg = new Egg(null, LifeCycle.DAY_BECOME_CHICKEN, Store.PRECIO_VENTA_EGG,Store.PRECIO_COMPRA_EGG, null, farm);
            eggRepository.save(egg);
            listEggsBuy.add(egg);
        }
        getPrecioTotalComprado(listEggsBuy);
        /*  Actualizando Farm.  */
        farm.setDinero(farm.getDinero() - (cantidad * Store.PRECIO_COMPRA_EGG));
        farm.setGastos(farm.getGastos() + (cantidad * Store.PRECIO_COMPRA_EGG));
        farm.setCantHuevos(farm.getCantHuevos() + cantidad);

        farmRepository.save(farm);
    }

    @Override
    public void sell(Farm farm, int cantidad) {
        List<Egg> listEgg = farm.getListEggs();
        List<Egg> listEggAEliminar = new ArrayList<>();

        verifyCantidadPositiva(cantidad);
        verifyStockForSell(cantidad, listEgg.size());

        /*  Vendiendo huevos.   */
        for (int i = 0; i < cantidad; i++){
            Egg egg = listEgg.get(i);
            listEggAEliminar.add(egg);
        }

        getPrecioTotalVendido(listEggAEliminar);
        eggRepository.deleteAll(listEggAEliminar);
        farm.getListEggs().removeAll(listEggAEliminar);

        /*  Actualizando los datos de la Farm.  */
        farm.setDinero(farm.getDinero() + (cantidad * Store.PRECIO_VENTA_EGG));
        farm.setCantHuevos(farm.getCantHuevos() - cantidad);
        farm.setCantHuevosVendidos(farm.getCantHuevosVendidos() + cantidad);
        farmRepository.save(farm);
    }

    @Override
    public void sellExcedent(Farm farm, int newEggs, int excedent) {
        double sellPrice = (Store.PRECIO_VENTA_EGG/2);

        List<Egg> listEggs = farm.getListEggs();
        listEggs.sort(Comparator.comparing(Egg::getId).reversed());

        for (Egg egg : listEggsToSell) {
            egg.setPrecio(sellPrice);
        }
        getPrecioTotalVendido(listEggsToSell);

        /*  Actualizando los datos de la Farm.  */
        farm.setCantHuevos(newEggs); //farm.setCantHuevos(farm.getCantHuevos() + newEggs);
        farm.setCantHuevosVendidos(farm.getCantHuevosVendidos() + excedent);
        farm.setDinero(farm.getDinero() + (excedent * sellPrice));
        farm.setListEggs(eggRepository.findAll());
        farm.setListChickens(chickenRepository.findAll());//

        farm.setCantPollos(farm.getCantPollos() + countChicken); //Revisar countChickens.

        logger.info("Exceso de Huevos: " + excedent + " se venderan a un precio total de $" + (excedent * sellPrice));
        /* --------- [test]---------*/
        logger.info("Total Chickens: " + chickenRepository.findAll().size() + ". Se deben eliminar " + countChickensSell);

        double sellPriceChicken = Store.PRECIO_VENTA_CHICKEN/2;
        for (Chicken chicken : listChickensToSell){
            chicken.setPrecio(sellPriceChicken);
        }
        chickenService.getPrecioTotalVendido(listChickensToSell);

        farm.setCantPollosVendidos(farm.getCantPollosVendidos() + countChickensSell);
        farm.setListChickens(chickenRepository.findAll());
        farm.setCantPollos(chickenRepository.findAll().size());
        farm.setDinero(farm.getDinero() + (countChickensSell * sellPriceChicken));
    }

    public void diasEnConvertirseEnPollo(Farm farm){
        List<Egg> eggsToConvert = new ArrayList<>();
        int contadorPollos = 0;

        for (Egg egg : farm.getListEggs()){//
            if (egg.getDiasEnConvertirseEnPollo() <= 1){
                eggsToConvert.add(egg);
                contadorPollos++;
            }
            egg.setDiasEnConvertirseEnPollo(egg.getDiasEnConvertirseEnPollo() - 1);
        }

        // Crear nuevos Pollos
        for (int i = 0; i < contadorPollos; i++){
            Chicken chicken = new Chicken(null, LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_VENTA_CHICKEN, Store.PRECIO_COMPRA_CHICKEN, null, farm);
            farm.getListChickens().add(chicken);
            chickenRepository.save(chicken);
            if (farm.getCantPollos() + countChicken >= farm.getLimitePollos()){
                listChickensToSell.add(chicken);//TESTANDO
                countChickensSell +=1;
            }else {
                countChicken += 1;
            }
        }

        // Eliminar los huevos convertidos
        for (Egg egg : eggsToConvert) {
            Chicken chicken = egg.getChicken();
            if (chicken != null) {
                chicken.getListEggs().remove(egg);
            }
            farm.getListEggs().remove(egg);
            eggRepository.delete(egg);
            TemporalVariables.countEggs -= 1;
        }

        //[PROBANDO] ...Chickens a vender
        for (Chicken chicken : listChickensToSell){
            farm.getListChickens().remove(chicken);
            chickenRepository.delete(chicken);
            //countChicken -=1; // [test]
        }
    }
}
