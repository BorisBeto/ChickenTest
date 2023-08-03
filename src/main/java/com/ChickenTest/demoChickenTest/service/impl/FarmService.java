package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.controller.FarmController;
import com.ChickenTest.demoChickenTest.dto.FarmDashboardDto;
import com.ChickenTest.demoChickenTest.dto.FarmDto;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Egg;
import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.entity.Tienda;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import org.hibernate.collection.spi.PersistentSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;


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
                    double precioChicken = Tienda.PRECIO_COMPRA_CHICKEN;

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
                    double precioEgg = Tienda.PRECIO_COMPRA_EGG;

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

                    logger.info("Granja: " + farm);

                    farm.setDinero(dineroDisponible + (Tienda.PRECIO_VENTA_CHICKEN * cantidad));
                    farm.setCantPollos(farm.getCantPollos() - cantidad);
                    logger.info("Granja actualizado: " + farm);

                    farmRepository.save(farm);


                }else {
                    throw new RuntimeException("La cantidad de pollos a vender debe ser menor o igual a la cantidad actual que posee en la granja");
                }
            }
        }
    }
}
