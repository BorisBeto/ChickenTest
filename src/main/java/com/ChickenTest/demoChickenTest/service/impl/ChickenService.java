package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.ChickenDto;
import com.ChickenTest.demoChickenTest.dto.TransaccionDto;
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
import java.util.Comparator;
import java.util.List;

import static com.ChickenTest.demoChickenTest.component.TemporalVariables.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ChickenService implements ITransaction{
    private static final Logger logger = Logger.getLogger(ChickenService.class);
    @Autowired
    IEggRepository eggRepository;
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    private IFarmRepository farmRepository;
    @Autowired
    ObjectMapper mapper;

    double precioTotalVendido = 0.0;
    int cantidadVendidos = 0;
    double precioTotalComprado = 0.0;
    int cantidadComprados = 0;

    public List<ChickenDto> getDataTableChicken(){

        List<Chicken> chickens = chickenRepository.findAll();
        List<ChickenDto> chickenDtos = new ArrayList<>();

        for (Chicken chicken : chickens){
            ChickenDto chickenDto = mapper.convertValue(chicken, ChickenDto.class);
            chickenDtos.add(chickenDto);
        }

        return chickenDtos;
    }
    protected void verifyStock(int cantidad, int stockActual, int limiteStock){
        if ((stockActual + cantidad) > limiteStock){
            logger.error("Supero el Limite de Stock de Pollos. Cantidad: " + cantidad + ". Stock actual de Pollos: " + stockActual + " .Limite de Stock de Pollos: " + limiteStock);
            throw new RuntimeException("Supero la cantidad Máxima de Pollos.");
        }
    }
    protected boolean isLimiteStock(int cantidad, int stockActual, int limiteStock){
        return (stockActual + cantidad) > limiteStock;
    }
    private void verifyCantidadPositiva(int cantidad){
        if (cantidad <= 0){
            logger.error("Error, la cantidad ingresada de ser Entero positivo. Numero ingresado: " + cantidad);
            throw new RuntimeException("La cantidad ingresada debe ser Entero positivo.");
        }
    }
    private void verifyDineroDisponible(double dineroDisponible, double costoTotal){
        if (dineroDisponible < costoTotal){
            logger.error("Saldo insuficiente. Monto total: $" + costoTotal + ". Dinero disponible: $" + dineroDisponible);
            throw new RuntimeException("Dinero disponible insuficiente.");
        }
    }
    private void verifyStockSell(int cantidad, int stockActual){
        if (stockActual <= 0){
            logger.error("Actualmente no contiene Pollos en su granja. Stock actual: " + stockActual);
            throw new RuntimeException("Actualmente no posee Pollos en su granja.");
        } else if (cantidad > stockActual) {
            logger.error("No fue posible realizar la venta. Cantidad a vender: " + cantidad + " .Stock actual: " + stockActual);
            throw new RuntimeException("No tiene suficientes Pollos para vender.");
        }
    }
    public void getPrecioTotalComprado(List<Chicken> listChickensBuy){
        for (Chicken chicken : listChickensBuy){
            precioTotalComprado += chicken.getPrecioComprado();
        }

        cantidadComprados += listChickensBuy.size();
    }
    public void getPrecioTotalVendido(List<Chicken> listChickensSell){
        for (Chicken chicken : listChickensSell){
            precioTotalVendido += chicken.getPrecio();
        }

        cantidadVendidos += listChickensSell.size();
    }
    public void updatePrice(double newPriceForBuy, double newPriceForSell){
        List<Chicken> updatePriceChickens = chickenRepository.findAll();
        for (Chicken chicken : updatePriceChickens){
            Store.PRECIO_COMPRA_CHICKEN = newPriceForBuy;
            Store.PRECIO_VENTA_CHICKEN = newPriceForSell;

            //chicken.setPrecioComprado(Store.PRECIO_COMPRA_CHICKEN);
            chicken.setPrecio(Store.PRECIO_VENTA_CHICKEN);
            chickenRepository.save(chicken);
        }
    }


    @Override
    public void buy(Farm farm, int cantidad) {
        int cantidadChicken = farm.getCantPollos();
        int limiteChicken = farm.getLimitePollos();

        verifyCantidadPositiva(cantidad);
        verifyStock(cantidad, cantidadChicken, limiteChicken);
        verifyDineroDisponible(farm.getDinero(), cantidad * Store.PRECIO_COMPRA_CHICKEN);

        /*  Comprando N Chickens.   */
        List<Chicken> listChickensBuy = new ArrayList<>();
        for (int i = 0; i < cantidad; i++){
            Chicken chicken = new Chicken(null, LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_VENTA_CHICKEN, Store.PRECIO_COMPRA_CHICKEN, null, farm);
            chickenRepository.save(chicken);
            listChickensBuy.add(chicken);
        }
        getPrecioTotalComprado(listChickensBuy);
        /*  Actulizando Farm.   */
        farm.setDinero(farm.getDinero() - (cantidad * Store.PRECIO_COMPRA_CHICKEN));
        farm.setGastos(farm.getGastos() + (cantidad * Store.PRECIO_COMPRA_CHICKEN));
        farm.setCantPollos(farm.getCantPollos() + cantidad);

        farmRepository.save(farm);
    }
    @Override
    public void sell(Farm farm, int cantidad) {
        List<Chicken> listChicken = farm.getListChickens();
        List<Chicken> listChickenAEliminar = new ArrayList<>();

        verifyCantidadPositiva(cantidad);
        verifyStockSell(cantidad, listChicken.size());

        /*  Vendiendo Chickens.   */
        for (int i = 0; i < cantidad; i++){
            Chicken chicken = listChicken.get(i);
            // Desvincular los huevos relacionados del pollo
            for (Egg egg : chicken.getListEggs()) {
                egg.setChicken(null);
                eggRepository.save(egg);
            }
            listChickenAEliminar.add(chicken);
        }
        getPrecioTotalVendido(listChickenAEliminar);
        chickenRepository.deleteAll(listChickenAEliminar);
        farm.getListChickens().removeAll(listChickenAEliminar);

        /*  Actualizando Farm.  */
        farm.setDinero(farm.getDinero() + (Store.PRECIO_VENTA_CHICKEN * cantidad));
        farm.setCantPollos(farm.getCantPollos() - cantidad);
        farm.setCantPollosVendidos(farm.getCantPollosVendidos() + cantidad);

        farmRepository.save(farm);
    }
    @Override
    public void sellExcedent(Farm farm, int newChickens, int excedent) {
        List<Chicken> listChickensRemove = new ArrayList<>();
        double sellPrice = Store.PRECIO_VENTA_CHICKEN/2;
        double sellPriceEggs = Store.PRECIO_VENTA_EGG/2;

        for (Chicken chicken : listChickensToSell){
                for (Egg egg : farm.getListEggs()){
                    egg.setChicken(null);
                    eggRepository.save(egg);
                }
                chicken.setPrecio(sellPrice);
                listChickensRemove.add(chicken);
                //farm.getListChickens().remove(chicken);
                chickenRepository.delete(chicken);
        }

        getPrecioTotalVendido(listChickensRemove);
        farm.getListChickens().removeAll(listChickensRemove);

        /*  Actualizando los datos de la Farm.  */
        if (!isExccessEggs){
            farm.setCantHuevos(countEggs);
            farm.setCantHuevosVendidos(farm.getCantHuevosVendidos() + countEggsSell);
            farm.setDinero(farm.getDinero() + (countEggsSell * sellPriceEggs));
        }


        farm.setCantPollos(newChickens);
        farm.setCantPollosVendidos(farm.getCantPollosVendidos() + excedent);
        farm.setDinero(farm.getDinero() + (excedent * sellPrice));

        farm.setListEggs(eggRepository.findAll());
        farm.setListChickens(chickenRepository.findAll());

        logger.info("Exceso de Pollos: " + excedent + " se venderan a un precio total de $" + (excedent * sellPrice));
    }
}
