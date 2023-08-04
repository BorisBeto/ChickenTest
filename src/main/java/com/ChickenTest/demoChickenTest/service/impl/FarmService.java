package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.FarmDashboardDto;
import com.ChickenTest.demoChickenTest.entity.*;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class FarmService {
    private static final Logger logger = Logger.getLogger(FarmService.class);
    @Autowired
    IFarmRepository farmRepository;
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    IEggRepository eggRepository;

    @Autowired
    ObjectMapper mapper;

    public FarmDashboardDto getPropertiesDashboard(){
        /*  1. Obtener la granaja   */
        List<Farm> listFarm = farmRepository.findAll();
        Farm farm = listFarm.stream().findFirst().get();

        /*  2. Obtener la cantidad de Pollos y Huevos que tiene la granja   */
        int cantidadHuevos = farm.getListEggs().size();
        int cantidadPollos = farm.getListChickens().size();

        /*  3. Asigno, cantidad de Pollos y Huevos a mi Dto FarmDashboardDto.
         *  Dolverá un objeto Dto para ser mostrado en la vista */
        FarmDashboardDto farmDashboardDto = mapper.convertValue(farm, FarmDashboardDto.class);

        farmDashboardDto.setCantHuevos(cantidadHuevos);
        farmDashboardDto.setCantPollos(cantidadPollos);

        logger.info("obteniendo datos propiedades Dashboard ...");

        return farmDashboardDto;
    }

    public void buy(String tipo, int cantidad){
        /*  1. Obtener la granaja datos de la granaja */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        double dineroDisponible = farm.getDinero();

        if(farm != null) {
            if(tipo == "chicken"){
                /*  2. Obtener stock Pollos */
                int cantidadChicken = farm.getCantPollos();
                int limiteChicken = farm.getLimitePollos();

                logger.info("Verificando Chicken Stock ...");

                /*  3. Verificar Stock  */
                if (cantidadChicken + cantidad <= limiteChicken){
                    double precioChicken = Store.PRECIO_COMPRA_CHICKEN;

                    logger.info("Verificando Cash disponible ...");

                    /*  4. Verificar dinero disponible del granjero */
                    if (dineroDisponible > (cantidad * precioChicken)){
                        for (int i = 0; i < cantidad; i++){
                            chickenRepository.save(new Chicken(null,20,5,precioChicken,null, farm)); // new WeakReference<>(farm).get())
                            logger.info("Chicken comprado.");
                        }
                        logger.info("Actualizando datos de la granja ...");

                        /*  5. Actualizar estado de la granja.  */
                        farm.setDinero(dineroDisponible - (cantidad * precioChicken));
                        farm.setCantPollos(farm.getCantPollos() + cantidad); // cantPollos actual + cantidad comprado
                        logger.info("Granja actualizado: " + farm);
                        farmRepository.save(farm);
                    }else{
                        logger.error("Dinero insuficiente");
                        throw new RuntimeException("Dinero disponible insuficiente");
                    }
                }else {
                    logger.error("Supero el Chicken Stock");
                    throw new RuntimeException("Supero la capacidad Máxima de Pollos");
                }
            } else if (tipo == "egg") {
                /*  2. Obtener stock Huevos */
                int cantidadEgg = farm.getCantHuevos();
                int limitEgg = farm.getLimiteHuevos();

                logger.info("Verificando Egg Stock ...");
                /*  3. Verificar Stock  */
                if (cantidadEgg + cantidad <= limitEgg){
                    logger.info("Verificando Cash disponible ...");
                    double precioEgg = Store.PRECIO_COMPRA_EGG;

                    /*  4. Validar dinero disponible del granjero   */
                    if (dineroDisponible > (cantidad * precioEgg)){
                        for (int i = 0; i < cantidad; i++){
                            eggRepository.save(new Egg(null,20,50,null,farm));  // Creación de nuevos Egg [pasar paramtros]
                            logger.info("Egg comprado.");
                        }
                        logger.info("Actualizando datos de la granja ...");
                        /*  5. Actualizar estado de la granja.  */
                        farm.setDinero(dineroDisponible - (cantidad * precioEgg));
                        farm.setCantHuevos(farm.getCantHuevos() + cantidad);
                        logger.info("Granja actualizado: " + farm);
                        farmRepository.save(farm);
                    }else{
                        logger.error("Dinero insuficiente");
                        throw new RuntimeException("Dinero disponible insuficiente");
                    }
                }else {
                    logger.error("Supero el Egg Stock");
                    throw new RuntimeException("Supero la capacidad Máxima de Huevos");
                }
            }
        }else{
            logger.error("No es posible comprar Huevos ni Pollos.");
            throw new RuntimeException("No hay ninguna granaja registrada");
        }

    }

    public void sell(String tipo, int cantidad){
        /*  1. Obtener los datos de la granja.  */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        double dineroDisponible = farm.getDinero();

        if (farm != null){
            if (tipo == "chicken"){
                /*  2. Obtener la lista de Pollos que posee la granaja. */
                List<Chicken> listChicken = farm.getListChickens();

                /*  3. Verificar la cantidad de Pollos disponibles para vender  */
                if (cantidad <= listChicken.size()){
                    for(int i = 0; i < cantidad; i++){
                        chickenRepository.deleteById(listChicken.get(i).getId());
                        farm.getListChickens().remove(listChicken.get(i));
                    }

                    farm.setDinero(dineroDisponible + (Store.PRECIO_VENTA_CHICKEN * cantidad));
                    farm.setCantPollos(farm.getCantPollos() - cantidad);
                    logger.info("Granja: " + farm);

                    farmRepository.save(farm);


                }else {
                    throw new RuntimeException("La cantidad de pollos a vender debe ser menor o igual a la cantidad actual que posee en la granja");
                }
            }else if (tipo == "egg"){
                /*  2. Obtener lista de Huevos que posee la granja. */
                List<Egg> listEgg = farm.getListEggs();

                /*  3. Verificar la cantidad de Pollos disponibles para vender  */
                if (cantidad <= listEgg.size()){
                    for (int i = 0; i < cantidad; i++){
                        eggRepository.deleteById(listEgg.get(i).getId());
                        farm.getListEggs().remove(listEgg.get(i));
                    }

                    farm.setDinero(dineroDisponible + (Store.PRECIO_VENTA_EGG * cantidad));
                    farm.setCantPollos(farm.getCantHuevos() - cantidad);
                    logger.info("Granja: " + farm);

                    farmRepository.save(farm);
                }else{
                    throw new RuntimeException("La cantidad de Huevos a vender debe ser menor o igual a la cantidad actual que posee en la granja");
                }
            }else {
                throw new RuntimeException("Solicitud denegada. Debe seleccionar 'chicken' o 'egg'.");
            }
        }else {
            logger.error("No es posible comprar Huevos ni Pollos. No hay granja registrada");
            throw new RuntimeException("No hay ninguna granaja registrada");
        }
    }

    public void pasarDias(int cantidad){
        /*  1. Obtener datos de la Granja.  */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        List<Chicken> listChicken = farm.getListChickens();



        if (farm != null){
            for (int i = 0; i < cantidad; i++){ //Recorriendo días ...

                /*  Verificar si hubo muertos*/
                for (int j = 0; j < listChicken.size(); j++){
                    if (listChicken.get(j).getDiasDeVida() <= 0){
                        logger.info("chicken muerto.");
                        chickenRepository.deleteById(listChicken.get(j).getId());
                        farm.getListChickens().remove(listChicken.get(j));  //  Elimino el Chicken de la granja, de mi lista de Chickens.

                    }else{

                        logger.info("Pollo" + j + ": " + listChicken.get(j).getDiasDeVida());
                        listChicken.get(j).setDiasDeVida( listChicken.get(j).getDiasDeVida() - 1 ); // Actualizo los dias de vida de cada Chicken.

                    }
                }

                logger.info("Paso Dia" + i);
            }
            logger.info("Cantidad Pollos en la granja: " + farm.getCantPollos() + " | " + "Lista de Pollos: " + listChicken.size());

            farm.setCantPollos(listChicken.size());
            farmRepository.save(farm);
        }
    }
}
