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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class EggService implements ITransaction {
    @Autowired
    IEggRepository eggRepository;
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    IFarmRepository farmRepository;
    @Autowired
    ObjectMapper mapper;

    public List<EggDto> getDataTableEgg(){
        List<Egg> eggs = eggRepository.findAll();
        List<EggDto> eggDtos = new ArrayList<>();

        for (Egg egg : eggs){
            EggDto eggDto = mapper.convertValue(egg, EggDto.class);
            eggDtos.add(eggDto);
        }

        return eggDtos;
    }

    public void verifyStock(int cantidad, int stockActual, int limiteStock){
        if ((stockActual + cantidad) > limiteStock){
            throw new RuntimeException("Supero la capacidad Máxima de Huevos");
        }
    }

    public boolean isLimiteSotck(int cantidad, int stockActual, int limiteStock){
        if ((stockActual + cantidad) > limiteStock){
            return true;
        }
        return false;
    }

    private void verifyDineroDisponible(double dineroDisponible, double costoTotal){
        if (dineroDisponible < costoTotal){
            throw new RuntimeException("Dinero disponible insuficiente.");
        }
    }

    private void verifyStockForSell(int cantidad, int stockActual){
        if (stockActual <= 0){
            throw new RuntimeException("Actualmente no posee Huevos en su granja.");
        } else if (cantidad > stockActual) {
            throw new RuntimeException("No tiene suficientes huevos para vender.");
        }
    }

    @Override
    public void buy(Farm farm, int cantidad) {
        int cantidadEgg = farm.getCantHuevos();
        int limiteEgg = farm.getLimiteHuevos();

        /*  Verificando Egg Stock   */
        verifyStock(cantidad, cantidadEgg, limiteEgg);

        /*  Verificando Cash Disponible */
        verifyDineroDisponible(farm.getDinero(), cantidad * Store.PRECIO_COMPRA_EGG);

        /*  Comprando Huevos.   */
        for (int i = 0; i < cantidad; i++){
            eggRepository.save(new Egg(null, LifeCycle.DAY_BECOME_CHICKEN, Store.PRECIO_COMPRA_EGG, null, farm));
        }

        /*  Actualizando datos de la Farm.  */
        farm.setDinero(farm.getDinero() - (cantidad * Store.PRECIO_COMPRA_EGG));
        farm.setCantHuevos(farm.getCantHuevos() + cantidad);
        farmRepository.save(farm);
    }

    @Override
    public void sell(Farm farm, int cantidad) {
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
        farm.setDinero(farm.getDinero() + (cantidad * Store.PRECIO_VENTA_EGG));
        farm.setCantHuevos(farm.getCantHuevos() - cantidad);
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
                //verifyStock(1, farm.getCantPollos() + contadorPollos, farm.getLimitePollos()); // [TEST]
                listEggAEliminar.add(egg);
                chickenRepository.save(new Chicken(null, LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_COMPRA_CHICKEN, null, farm));
                farm.getListEggs().remove(egg);
                contadorPollos++;
            }

            egg.setDiasEnConvertirseEnPollo(egg.getDiasEnConvertirseEnPollo() - 1);
        }

        eggRepository.deleteAll(listEggAEliminar);
    }
}
