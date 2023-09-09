package com.ChickenTest.demoChickenTest.service.impl;

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
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EggService implements ITransaction {
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
    public void sellExcedent(Farm farm, int cantidad, double precio) {
        List<Egg> listEgg = farm.getListEggs();
        List<Egg> listEggAEliminar = new ArrayList<>();

        /*  Verificando Egg Stock   */
        verifyStockForSell(cantidad, listEgg.size());

        /*  Vendiendo huevos.   */
        for (int i = 0; i < cantidad; i++){
            Egg egg = listEgg.get(i);
            listEggAEliminar.add(egg);
            //eggRepository.deleteById(egg.getId());
            //farm.getListEggs().remove(egg);
        }

        eggRepository.deleteAll(listEggAEliminar);
        farm.getListEggs().removeAll(listEggAEliminar);

        /*  Actualizando los datos de la Farm.  */
        farm.setDinero(farm.getDinero() + (cantidad * (precio/2))); //  Vendido a mitad de Precio.
        farm.setCantHuevos(farm.getCantHuevos() - cantidad);
        farmRepository.save(farm);
    }
    public void diasEnConvertirseEnPollo(int dias, Farm farm){
        List<Egg> listEgg = eggRepository.findAll();
        List<Egg> listEggAEliminar = new ArrayList<>();
        int contadorPollos = 1;

        for (Egg egg : listEgg){
            if (egg.getDiasEnConvertirseEnPollo() <= 1){
                listEggAEliminar.add(egg);
                chickenRepository.save(new Chicken(null, LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_VENTA_CHICKEN, Store.PRECIO_COMPRA_CHICKEN, null, farm));
                farm.getListEggs().remove(egg);
                contadorPollos++;
            }
            egg.setDiasEnConvertirseEnPollo(egg.getDiasEnConvertirseEnPollo() - 1);
        }

        eggRepository.deleteAll(listEggAEliminar);
    }
}
