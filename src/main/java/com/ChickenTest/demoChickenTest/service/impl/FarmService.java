package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.FarmDashboardDto;
import com.ChickenTest.demoChickenTest.dto.FarmTableDto;
import com.ChickenTest.demoChickenTest.entity.*;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class FarmService {
    private static final Logger logger = Logger.getLogger(FarmService.class);
    @Autowired
    IFarmRepository farmRepository;

    @Autowired
    private ChickenService chickenService;
    @Autowired
    private EggService eggService;
    @Autowired
    ObjectMapper mapper;



    @Autowired
    IChickenRepository chickenRepository;   //  Pendiente de eliminar.
    @Autowired
    IEggRepository eggRepository;           //  Pendiente de eliminar.

    public FarmTableDto getDataTableFarm(){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));

        FarmTableDto farmTableDto = mapper.convertValue(farm, FarmTableDto.class);

        return farmTableDto;
    }

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

        return farmDashboardDto;
    }

    public void buy(String tipo, int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( ()-> new RuntimeException("No hay ninguna granja registrada."));

        if (tipo.equals("chicken")){
            chickenService.buy(farm, cantidad);
        } else if (tipo.equals("egg")) {
            eggService.buy(farm, cantidad);
        }else {
            throw new IllegalArgumentException("Solicitud denegada. Debe seleccionar  'chicken' o 'egg'");
        }
    }

    public void sell(String tipo, int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( () -> new RuntimeException("No hay ninguna granja registrada."));

        if (tipo.equals("chicken")){
            chickenService.sell(farm, cantidad);
        } else if (tipo.equals("egg")) {
            eggService.sell(farm, cantidad);
        }else {
            throw new IllegalArgumentException("Solicitud denegada. Debe seleccionar 'chicken' o 'egg'.");
        }
    }


    private boolean isGranjaExpirada(Farm farm, int cantidad){
        return cantidad > farm.getDias();
    }

    private void updateChickenStatus(List<Chicken> listChicken, Farm farm){
        for (Chicken chicken : listChicken){
            chicken.setDiasDeVida(chicken.getDiasDeVida() - 1);

            if (chicken.getDiasDeVida() < LifeCycle.DAY_OF_LIFE_CHICKEN && (chicken.getDiasDeVida() % chicken.getDiasParaPonerHuevos()) == 0){
                eggRepository.save(new Egg(null, (LifeCycle.DAY_BECOME_CHICKEN + 1), Store.PRECIO_COMPRA_EGG, chicken, farm));
            }

        }
    }

    private void removeDeadChickens(List<Chicken> listChicken, Farm farm){
        List<Chicken> pollosAEliminar = new ArrayList<>();

        for (Chicken chicken : listChicken){
            if (chicken.getDiasDeVida() <= 0){
                chicken.getListEggs().forEach( egg -> {
                    egg.setChicken(null);
                    /*  Desvinculando Huevo de Pollo.   */
                });

                eggRepository.saveAll(chicken.getListEggs());
                pollosAEliminar.add(chicken);
                /*  Chicken ha muerto   */
            }
        }

        for (Chicken chicken : pollosAEliminar){
            farm.getListChickens().remove(chicken);
            chickenRepository.delete(chicken);
        }

    }

    private void updateFarmData(Farm farm, int diasDeVidaGranja, int cantidad){
        /*  Actualizando los datos de la Farm.  */
        farm.setCantHuevos(eggRepository.findAll().size());
        farm.setDias(diasDeVidaGranja - cantidad);
        farm.setCantPollos(chickenRepository.findAll().size());
    }

    private int verifyExcess(int cantidad, int cantidadPollos, int limitePollos, int cantidadHuevos, int limiteHuevos){
        boolean isChickenStock = chickenService.isLimiteStock(cantidadHuevos, cantidadPollos, limitePollos);  //  Reemplazar cantidad (dias) por cantidad a vender
        boolean isEggStock = eggService.isLimiteSotck(cantidadPollos, cantidadHuevos, limiteHuevos);    //  [TEST]

        if (isChickenStock && isEggStock){
            logger.info("Superó la cantidad Máxima de Pollos y Huevos disponibles en la granaja.");
            return 2;
        } else if (isChickenStock) {
            logger.info("Superó la cantidad Máxima de Pollos disponibles en la granja.");
            return 1;
        } else if (isEggStock) {
            logger.info("Superó la cantidad Máxima de Huevos disponibles en la granja.");
            return -1;
        }

        return 0;
    }

    private void venderConDescuento(Farm farm, int cantidad){
        int option = verifyExcess(cantidad, farm.getListChickens().size(), farm.getLimitePollos(), farm.getListEggs().size(), farm.getLimiteHuevos()); // [TEST]

        if (option == 2){
            chickenService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_CHICKEN);
            eggService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_EGG);
        }else if (option == 1){
            chickenService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_CHICKEN);
        } else if (option == -1) {
            eggService.sellExcedent(farm, (farm.getCantHuevos() + farm.getCantPollos()) - farm.getLimiteHuevos(), Store.PRECIO_VENTA_EGG);  // Disponibilizar Stock y vender.
        }

    }

    public void pasarDias(int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));
        List<Chicken> listChicken = farm.getListChickens();
        List<Egg> listEgg = farm.getListEggs();
        int diasDeVidaGranja = farm.getDias();

        //venderConDescuento(farm, cantidad); //  Vendiendo excedente con descuento...[TEST]

        for (int i=0; i < cantidad; i++){
            if (!isGranjaExpirada(farm, cantidad)){
                updateChickenStatus(listChicken, farm);
                removeDeadChickens(listChicken, farm);
            }else {
                for (Egg egg : listEgg){
                    egg.setFarm(null);
                    eggRepository.save(egg);
                }
                for (Chicken chicken : listChicken){
                    chicken.setFarm(null);
                    chickenRepository.save(chicken);
                }
                farm.setDias(0);
                farmRepository.save(farm);
                throw new RuntimeException("El dueño de la granja acaba de irse.");
            }
            eggService.diasEnConvertirseEnPollo(i, farm);
        }
        updateFarmData(farm, diasDeVidaGranja, cantidad);
        farmRepository.save(farm);
    }


































    public void buyVersionOld(String tipo, int cantidad){
        /*  1. Obtener la granaja datos de la granaja */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        double dineroDisponible = farm.getDinero();

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
                            chickenRepository.save(new Chicken(null,LifeCycle.DAY_OF_LIFE_CHICKEN,LifeCycle.DAY_TO_LAY_EGGS,precioChicken,null, farm)); // new WeakReference<>(farm).get())
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
                            eggRepository.save(new Egg(null,LifeCycle.DAY_BECOME_CHICKEN,precioEgg,null,farm));  // Creación de nuevos Egg [pasar paramtros]
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


    }

    public void sellVersionOld(String tipo, int cantidad){
        /*  1. Obtener los datos de la granja.  */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        double dineroDisponible = farm.getDinero();

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

    }

    public void pasarDiasVersionOld(int cantidad){
        /*  1. Obtener datos de la Granja.  */
        Farm farm = farmRepository.findAll().stream().findFirst().get();
        List<Chicken> listChicken = farm.getListChickens();
        int diasDeVidaGranja = farm.getDias();

        List<Chicken> pollosAEliminar = new ArrayList<>();

        for (int i = 0; i < cantidad; i++){
            logger.info("Dia " + i);


            if (cantidad < diasDeVidaGranja){
                /*  Verificar si hubo muertos o si pusieron huevos. */
                for (int j = 0; j < listChicken.size(); j++){   // En la ejecucion del pograma, al eliminar pollos, la lista de pollos cambia su valor.

                    listChicken.get(j).setDiasDeVida( listChicken.get(j).getDiasDeVida() - 1 );

                    if (listChicken.get(j).getDiasDeVida() <= 0){
                        //Elimar la relacion huevos y pollos.
                        Chicken chicken = listChicken.get(j);

                        for (Egg egg : chicken.getListEggs()){
                            egg.setChicken(null);
                            logger.info("Desvinculando huevo de Pollo.");
                        }

                        eggRepository.saveAll(chicken.getListEggs());

                        pollosAEliminar.add(chicken);
                        //chickenRepository.delete(chicken);
                        logger.info("chicken muerto.");
                        //farm.getListChickens().remove(chicken);

                    } else if (listChicken.get(j).getDiasDeVida() < LifeCycle.DAY_OF_LIFE_CHICKEN){

                        if ( (listChicken.get(j).getDiasDeVida() % listChicken.get(j).getDiasParaPonerHuevos()) == 0){  //  Si es múltiplo de 5 (chicken.diasParaPonerHuevos)
                            logger.info("Pollo: " + j + " ha puesto un huevo");
                            eggRepository.save( new Egg(null, (LifeCycle.DAY_BECOME_CHICKEN + 1), Store.PRECIO_COMPRA_EGG, listChicken.get(j), farm) );
                        }
                    }

                    logger.info("Pollo " + j + ", Dias de Vida: " + listChicken.get(j).getDiasDeVida());    //Pollo: 0 DiasVida: 17
                }

                for (Chicken chicken : pollosAEliminar){
                    farm.getListChickens().remove(chicken);
                    chickenRepository.delete(chicken);
                }

            }else{
                throw new RuntimeException("Los días de la granja han experidado. El dueño de la granja acaba de irse");
            }

            diasEnConvertirseEnPolloVersionOld(i);
        }

        logger.info("Datos de la granja vieja: " + farm);
        farm.setCantHuevos(eggRepository.findAll().size());
        farm.setDias(diasDeVidaGranja - cantidad);
        farm.setCantPollos(chickenRepository.findAll().size()); //listChicken.size()
        logger.info("Datos de la granja actualizada: " + farm);
        farmRepository.save(farm);
    }

    public void diasEnConvertirseEnPolloVersionOld(int dias){

        Farm farm = farmRepository.findAll().stream().findFirst().get();
        List<Egg> listEgg = eggRepository.findAll();
        List<Egg> listEggAEliminar = new ArrayList<>();

        int contadorPollos = 1;
        for (int i = 0; i < listEgg.size(); i++){

            Egg egg = listEgg.get(i);

            logger.info("Dia: " + dias + " Cantidad dias en convertirse en Pollo: " + egg.getDiasEnConvertirseEnPollo());

            if (egg.getDiasEnConvertirseEnPollo() <= 1){
                listEggAEliminar.add(egg);
                //  Crear Pollo
                chickenRepository.save(new Chicken(null,LifeCycle.DAY_OF_LIFE_CHICKEN,LifeCycle.DAY_TO_LAY_EGGS,Store.PRECIO_COMPRA_CHICKEN,null, farm));
                logger.info("Huevo se ha convertido en Pollo.");

                farm.getListEggs().remove(listEgg.get(i));
                //farm.setCantPollos(farm.getCantPollos() + contadorPollos);
                contadorPollos++;
            }

            egg.setDiasEnConvertirseEnPollo( egg.getDiasEnConvertirseEnPollo() - 1);


            //  Comparar(numero, numero a comparar): bool . ¿Que pasa si días de vida del pollo es Mayor a los dias en que se convierte en pollo?

            //  DeleteList(list, Repository): bool

        }
        eggRepository.deleteAll(listEggAEliminar);

    }


}
