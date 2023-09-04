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
import java.util.List;

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
            throw new RuntimeException("Supero la cantidad Máxima de Pollos.");
        }
    }

    protected boolean isLimiteStock(int cantidad, int stockActual, int limiteStock){
        if ((stockActual + cantidad) > limiteStock){
            return true;
        }

        return false;
    }
    private void verifyCantidadPositiva(int cantidad){
        if (cantidad <= 0){
            throw new RuntimeException("La cantidad ingresada debe ser Entero positivo.");
        }
    }

    private void verifyDineroDisponible(double dineroDisponible, double costoTotal){
        if (dineroDisponible < costoTotal){
            throw new RuntimeException("Dinero disponible insuficiente.");
        }
    }

    private void verifyStockSell(int cantidad, int stockActual){
        if (stockActual <= 0){
            throw new RuntimeException("Actualmente no posee Pollos en su granja.");
        } else if (cantidad > stockActual) {
            throw new RuntimeException("No tiene suficientes Pollos para vender.");
        }
    }
    @Override
    public void buy(Farm farm, int cantidad) {
        int cantidadChicken = farm.getCantPollos();
        int limiteChicken = farm.getLimitePollos();

        /*  Velidación de Cantidad a comprar   */
        verifyCantidadPositiva(cantidad);
        /*  Verificando Chicken Stock   */
        verifyStock(cantidad, cantidadChicken, limiteChicken);
        /*  Verificando Cash disponible */
        verifyDineroDisponible(farm.getDinero(), cantidad * Store.PRECIO_COMPRA_CHICKEN);

        /*  Comprando N Chickens.   */
        for (int i = 0; i < cantidad; i++){
            chickenRepository.save(new Chicken(null, LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_COMPRA_CHICKEN, null, farm));
        }

        /*  Actulizando datos de la Farm.   */
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

        /*  Vendiendo huevos.   */
        for (int i = 0; i < cantidad; i++){
            Chicken chicken = listChicken.get(i);
            // Desvincular los huevos relacionados del pollo
            for (Egg egg : chicken.getListEggs()) {
                egg.setChicken(null);
                eggRepository.save(egg);
            }
            listChickenAEliminar.add(chicken);
        }
        chickenRepository.deleteAll(listChickenAEliminar);
        farm.getListChickens().removeAll(listChickenAEliminar);

        /*  Actualizando datos de la Farm.  */
        farm.setDinero(farm.getDinero() + (Store.PRECIO_VENTA_CHICKEN * cantidad));
        farm.setCantPollos(farm.getCantPollos() - cantidad);
        farm.setCantPollosVendidos(farm.getCantPollosVendidos() + cantidad);
        farmRepository.save(farm);
    }

    @Override
    public void sellExcedent(Farm farm, int cantidad, double precio) {
        List<Chicken> listChicken = farm.getListChickens();
        List<Chicken> listChickenAEliminar = new ArrayList<>();

        /*  Verificando Chicken Stock   */
        verifyStockSell(cantidad, listChicken.size());

        logger.info("Cantidad de pollos a Eliminar: " + listChicken.size());
        /*  Vendiendo huevos.   */
        for (int i = 0; i < cantidad; i++){
            logger.info("Pollo " + (i + 1) + ": " + listChicken.get(i));
            Chicken chicken = listChicken.get(i);
            listChickenAEliminar.add(chicken);
        }

        chickenRepository.deleteAll(listChickenAEliminar);
        farm.getListChickens().removeAll(listChickenAEliminar);

        /*  Actualizando datos de la Farm.  */
        farm.setDinero(farm.getDinero() + ((precio/2) * cantidad)); // vendido a mitad de Precio.
        farm.setCantPollos(farm.getCantPollos() - cantidad);
        logger.info("Datos de la granja: " + farm);
        farmRepository.save(farm);
    }
}
