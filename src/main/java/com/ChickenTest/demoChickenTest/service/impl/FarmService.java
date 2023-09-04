package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.*;
import com.ChickenTest.demoChickenTest.entity.*;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.ChickenTest.demoChickenTest.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


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
    private ChickenService chickenService;
    @Autowired
    private EggService eggService;
    @Autowired
    ObjectMapper mapper;

    private int countBreakEggs = 0;
    private int countChickenDeads = 0;

    public FarmTableDto getDataTableFarm(){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));

        FarmTableDto farmTableDto = mapper.convertValue(farm, FarmTableDto.class);

        return farmTableDto;
    }

    public SectionCashAvailable getCashAvailable(){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));
        SectionCashAvailable cashAvailable = mapper.convertValue(farm, SectionCashAvailable.class);
        /*  Obtener ingresos y gastos, en función de lo comprado y vendido. */
        double precioTotalHuevosComprados = farm.getCantHuevos() * Store.PRECIO_COMPRA_EGG;
        double precioTotalPollosComprados = farm.getCantPollos() * Store.PRECIO_COMPRA_CHICKEN;

        /*cashAvailable.setNeto(cashAvailable.getDinero() - (precioTotalHuevosComprados + precioTotalPollosComprados));*/
        /*cashAvailable.setGastos(farm.getDinero() - cashAvailable.getNeto());*/
        cashAvailable.setSueldoBase(cashAvailable.getDinero() + farm.getGastos());

        return cashAvailable;
    }

    private int getDayPass(int cantidad){

        int diasTranscurridos = LifeCycle.DAY_OF_LIFE_FARMER - cantidad;

        return diasTranscurridos;
    }
    private String formatDate(int diasTranscurridos){ /*    Revisar ...*/
        /*  1. Obtener la fecha actual.    */
        LocalDate currentDate = LocalDate.now();

        /*  2. Incrementar dia a la fecha actual. */
        LocalDate increasedDate = currentDate.plusDays(diasTranscurridos);

        /*  3. Crear un formateador de fecha con el formato deseado. */
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM yy", new Locale("es", "ES"));
        /*  4. Formatear la fecha actual utilizando el formateador.  */
        String formattedDate = increasedDate.format(dateFormatter);

        return formattedDate;
    }

    public ChickenStatusDto getCardChickenStatus(){
        ChickenStatusDto chickenStatusDto = new ChickenStatusDto();

        chickenStatusDto.setCountChickensDead(countChickenDeads);
        chickenStatusDto.setCountBreakEggs(countBreakEggs);

        return chickenStatusDto;
    }

    public SectionBuySellProducts getSectionBuySellProducts(){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( ()-> new RuntimeException("No hay ninguna granja registrada."));
        SectionBuySellProducts sectionBuySellProducts = mapper.convertValue(farm, SectionBuySellProducts.class);

        double precioTotalHuevosComprados = farm.getCantHuevos() * Store.PRECIO_COMPRA_EGG;
        double precioTotalPollosComprados = farm.getCantPollos() * Store.PRECIO_COMPRA_CHICKEN;

        double precioTotalHuevosVendidos = farm.getCantHuevosVendidos() * Store.PRECIO_VENTA_EGG;
        double precioTotalPollosVendidos = farm.getCantPollosVendidos() * Store.PRECIO_VENTA_CHICKEN;

        sectionBuySellProducts.setPrecioTotalHuevosComprados(precioTotalHuevosComprados);
        sectionBuySellProducts.setPrecioTotalPollosComprados(precioTotalPollosComprados);

        sectionBuySellProducts.setPrecioTotalHuevosVendidos(precioTotalHuevosVendidos);
        sectionBuySellProducts.setPrecioTotalPollosVendidos(precioTotalPollosVendidos);

        return sectionBuySellProducts;
    }

    public FarmProgressDashboard getPropertiesProgressDashboard() {
        /*  1. Obtener la granaja   */
        List<Farm> listFarm = farmRepository.findAll();
        Farm farm = listFarm.stream().findFirst().get();

        /*  Serializar clase */
        FarmProgressDashboard farmProgressDashboard = mapper.convertValue(farm, FarmProgressDashboard.class);

        /*  3. Guardar los [días Transcurridos] [Cantidad Huevos] [cantidad Pollos]  */
        farmProgressDashboard.setDiasTranscurridos(getDayPass(farm.getDias())); //30
        farmProgressDashboard.setDiasVida(LifeCycle.DAY_OF_LIFE_FARMER);

        /*  4. Renderizar dashboard.    */
        double porcentajeDiasDeVida = ((farmProgressDashboard.getDiasVida() - farmProgressDashboard.getDiasTranscurridos()) * 1.0 / (farmProgressDashboard.getDiasVida())) * 100;
        farmProgressDashboard.setPorcentajeDiasVida(porcentajeDiasDeVida);

        double porcentajeHuevos = farm.getCantHuevos() * 100.0 / farm.getLimiteHuevos();
        farmProgressDashboard.setPorcentajeHuevos(porcentajeHuevos);

        double porcentajePollos = farm.getCantPollos() * 100.0 / farm.getLimitePollos();
        farmProgressDashboard.setPorcentajePollos(porcentajePollos);

        return farmProgressDashboard;
    }

    public FarmDashboardDto getPropertiesDashboard(){
        /*  1. Obtener la granaja   */
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( () -> new RuntimeException("No hay ninguna granja registrada."));

        /*  2. Obtener la cantidad de Pollos y Huevos que tiene la granja   */
        int cantidadHuevos = farm.getListEggs().size();
        int cantidadPollos = farm.getListChickens().size();

        /*  3. Asigno, cantidad de Pollos y Huevos a mi Dto FarmDashboardDto.
         *  Dolverá un objeto Dto para ser mostrado en la vista */
        FarmDashboardDto farmDashboardDto = mapper.convertValue(farm, FarmDashboardDto.class);

        int diasTranscurridos = getDayPass(farm.getDias());

        String fecha = formatDate(diasTranscurridos);

        farmDashboardDto.setFecha(fecha);

        farmDashboardDto.setCantHuevos(cantidadHuevos);
        farmDashboardDto.setCantPollos(cantidadPollos);

        return farmDashboardDto;
    }

    public ApiResponse<String> buy(String tipo, int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( ()-> new RuntimeException("No hay ninguna granja registrada."));
        try{
            if (tipo.equals("chicken")){
                chickenService.buy(farm, cantidad);
            } else if (tipo.equals("egg")) {
                eggService.buy(farm, cantidad);
            }else {
                throw new IllegalArgumentException("Solicitud denegada. Debe seleccionar  'chicken' o 'egg'");
            }

            return new ApiResponse<>(200, tipo + " comprado correctamente", "success");
        }catch (Exception e){
            return new ApiResponse<>(500, "No se pudo realizar la compra: " + e.getMessage(), "danger");
        }

    }

    public ApiResponse<String> sell(String tipo, int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow( () -> new RuntimeException("No hay ninguna granja registrada."));

        try{
            if (tipo.equals("chicken")){
                chickenService.sell(farm, cantidad);
            } else if (tipo.equals("egg")) {
                eggService.sell(farm, cantidad);
            }else {
                throw new IllegalArgumentException("Solicitud denegada. Debe seleccionar 'chicken' o 'egg'.");
            }

            return new ApiResponse<>(200, "Se vendieron " + cantidad + " " + tipo + " correctamente", "success");
        }catch (Exception e){
            return new ApiResponse<>(500, "No se pudo vender " + tipo + ": " + e.getMessage(), "danger");
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

        countChickenDeads = pollosAEliminar.size();
    }

    private void updateFarmData(Farm farm, int diasDeVidaGranja, int cantidad){
        /*  Actualizando los datos de la Farm.  */
        farm.setCantHuevos(eggRepository.findAll().size());
        farm.setDias(diasDeVidaGranja - cantidad);
        farm.setCantPollos(chickenRepository.findAll().size());
    }

    private void verifyExcess(int cantidad, Farm farm){
        boolean isChickenStock = chickenService.isLimiteStock(cantidad, farm.getCantPollos(), farm.getLimitePollos());  //  Reemplazar cantidad (dias) por cantidad a vender
        boolean isEggStock = eggService.isLimiteSotck(cantidad, farm.getCantHuevos(), farm.getLimiteHuevos());    //  [TEST]

        if (isChickenStock && isEggStock){
            logger.info("Superó la cantidad Máxima de Pollos y Huevos disponibles en la granaja.");
            throw new RuntimeException("Superó la cantidad Máxima de Pollos y Huevos disponibles en la granaja.");
        } else if (isChickenStock) {
            logger.info("Superó la cantidad Máxima de Pollos disponibles en la granja.");
            throw new RuntimeException("Superó la cantidad Máxima de Pollos disponibles en la granja.");
        } else if (isEggStock) {
            logger.info("Superó la cantidad Máxima de Huevos disponibles en la granja.");
            throw new RuntimeException("Superó la cantidad Máxima de Huevos disponibles en la granja.");
        }

    }

    private void venderConDescuento(Farm farm, int cantidad){
        int option = 0; //verifyExcess(cantidad, farm.getListChickens().size(), farm.getLimitePollos(), farm.getListEggs().size(), farm.getLimiteHuevos()); // [TEST]

        if (option == 2){
            chickenService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_CHICKEN);
            eggService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_EGG);
        }else if (option == 1){
            chickenService.sellExcedent(farm, cantidad, Store.PRECIO_VENTA_CHICKEN);
        } else if (option == -1) {
            eggService.sellExcedent(farm, (farm.getCantHuevos() + farm.getCantPollos()) - farm.getLimiteHuevos(), Store.PRECIO_VENTA_EGG);  // Disponibilizar Stock y vender.
        }

    }

    private void saveListEgg(List<Egg> listEgg){
        for (Egg egg : listEgg){
            egg.setFarm(null);
            eggRepository.save(egg);
        }
    }

    private void saveListChicken(List<Chicken> listChicken){
        for (Chicken chicken : listChicken){
            chicken.setFarm(null);
            chickenRepository.save(chicken);
        }
    }

    private void verifyCantidadPositiva(int cantidad){
        if (cantidad <= 0){
            throw new RuntimeException("Los días ingresados deben ser Enteros Positivos.");
        }
    }

    private void verifyStock(Farm farm, int cantidad){
        /*  1.  Obtener la lista de Chickens y Eggs de mi Granja.   */
        List<Chicken> listChicken = farm.getListChickens();
        List<Egg> listEgg = farm.getListEggs();
        int breakEggsCount = 0;
        int newEggsCount = 0;

        /*  2. Contar cuantos Huevos pasaron a ser Pollo.   */
        for(Egg egg : listEgg){
            if (egg.getDiasEnConvertirseEnPollo() <= cantidad){ //3 <= //5
                breakEggsCount++;
            }
        }

        /*  3. Contar cuantos Pollos pusieron Huevos.   */
        for (Chicken chicken : listChicken){
            if (chicken.getDiasParaPonerHuevos() <= cantidad){   // 5 <= //15
                newEggsCount += (cantidad / chicken.getDiasParaPonerHuevos()); /*  Int(Cantidad / diasParaPonerHuevos)*/
            }
        }

        /*  4. Calcular cuantos Huevos y Pollos tengo en mi granaja. */
        int countEggs = listEgg.size();
        int countChickens = listChicken.size();

        int stockEggs = ( countEggs - breakEggsCount) + newEggsCount; // (2 - 0) + 15
        int stockChickens = (countChickens + breakEggsCount);

        /*  5. Verificar si supero el Limite de Stock para Huevos.  */
        if (stockEggs > farm.getLimiteHuevos()){
            throw new RuntimeException("En el transcurso de los " + cantidad +" días se ha superado la capacidad Máxima de Huevos. Chickens han puesto " + newEggsCount + " nuevos Huevos.");
        }


        /*  6. Verificar si supero el Limite de Stock para Pollos.  */
        if (stockChickens > farm.getLimitePollos()){
            throw new RuntimeException("En el transcurso de los " + cantidad +" días se ha superado la capacidad Máxima de Pollos. Acaban de nacer " + newEggsCount + " Pollos.");
        }
        countBreakEggs = breakEggsCount;
    }

    public ApiResponse<String> updateUserName(String name){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));

        try{
                validateUserName(name);
                farm.setGranjero(name);
                farmRepository.save(farm);
                return new ApiResponse<>(200, "Se ha actualizado el nombre de usuario a '" + name + "'.", "success");
        }catch (Exception e){
            return new ApiResponse<>(500, e.getMessage(), "danger");
        }
    }

    public ApiResponse<String> updateFarmName(String name){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));

        try {
                validateFarmName(name);
                farm.setNombre(name);
                farmRepository.save(farm);
                return new ApiResponse<>(200, "Se ha actualizado el nombre de la app a  '" + name + "'.", "success");
        }catch (Exception e){
            return new ApiResponse<>(500, e.getMessage(), "danger");
        }
    }

    public ApiResponse<String>  updatePrices(String accion,String tipo, double precio){
        try {
            if (precio >= 1){
                if (accion.equals("buy")){
                    if (tipo.equals("chicken")){
                        Store.PRECIO_COMPRA_CHICKEN = precio;
                    }else if (tipo.equals("egg")){
                        Store.PRECIO_COMPRA_EGG = precio;
                    }else{
                        throw new RuntimeException("Por favor ingrese tipo correcto. 'chicken' or 'egg'.");
                    }
                } else if (accion.equals("sell")) {
                    if (tipo.equals("chicken")){
                        Store.PRECIO_VENTA_CHICKEN = precio;
                    }else if (tipo.equals("egg")){
                        Store.PRECIO_VENTA_EGG = precio;
                    }else{
                        throw new RuntimeException("Por favor ingrese tipo correcto. 'chicken' or 'egg'.");
                    }
                }else {
                    throw new RuntimeException("Por favor ingrese la accion requerida. 'buy' or 'sell'.");
                }

                return new ApiResponse<>(200, "Se ha actualizado el precio del " + tipo + " a $" + precio + ".", "success");
            }else {
                throw new RuntimeException("El precio debe ser positivo. Por favor, intente nuevamente.");
            }
        }catch (Exception e){
            return new ApiResponse<>(500, e.getMessage(), "danger");
        }
    }

    private void validateUserName(String userName){
        if (userName.length() < 3){
            throw new RuntimeException("El valor ingresado no parece ser un nombre. Por favor, intente nuevamente. La cantidad de digitos debe ser mayor o igual a 3.");
        }
    }

    private void validateFarmName(String farmName){
        if (farmName.length() < 3){
            throw new RuntimeException("El valor ingresado no parece ser un nombre. Por favor, intente nuevamente. La cantidad de digitos debe ser mayor o igual a 3.");
        }
    }

    private void validateDayOfLife(int dayOfLife){
        if (dayOfLife <= 0){
            throw new RuntimeException("Dias de vida inválidos. El valor ingresado debe ser Positivo. Por favor, intente nuevamente. ");
        } else if (dayOfLife < 10) {
            throw new RuntimeException("Por favor, intente nuevamente. El valor ingresado debe ser mayor o igual a 10.");
        }
    }

    private void validateChickensEggsCapacity(int amount){
        if (amount <= 0){
            throw new RuntimeException("El valor ingresado debe ser Positivo. Por favor, intente nuevamente.");
        } else if (amount < 10) {
            throw new RuntimeException("Por favor, intente nuevamente. Se recomienda tener tener una capacidad de 10 o más.");
        }
    }
    public ApiResponse<String> updatePerfil(String userName, String farmName, int dayOfLife, int eggsCapacity, int chickensCapacity) {
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));

        try{
            validateUserName(userName);
            validateFarmName(farmName);
            validateDayOfLife(dayOfLife);
            validateChickensEggsCapacity(chickensCapacity);
            validateChickensEggsCapacity(eggsCapacity);

            farm.setGranjero(userName);
            farm.setNombre(farmName);
            LifeCycle.DAY_OF_LIFE_FARMER = dayOfLife;
            farm.setDias(LifeCycle.DAY_OF_LIFE_FARMER);
            farm.setLimiteHuevos(eggsCapacity);
            farm.setLimitePollos(chickensCapacity);

            farmRepository.save(farm);
            return new ApiResponse<>(200, "Se ha actualizado el perfil exitosamente.", "success");
        }catch (Exception e){
            return new ApiResponse<>(500, e.getMessage(), "danger");
        }
    }

    public ApiResponse<String> pasarDias(int cantidad){
        Farm farm = farmRepository.findAll().stream().findFirst().orElseThrow(()-> new RuntimeException("Nose encontró ninguna granja registrada"));
        List<Chicken> listChicken = farm.getListChickens();
        List<Egg> listEgg = farm.getListEggs();
        int diasDeVidaGranja = farm.getDias();

        try{
            //venderConDescuento(farm, cantidad); //  Vendiendo excedente con descuento...[TEST]
            verifyStock(farm, cantidad);
            /*  Velidación de Cantidad positiva   */
            verifyCantidadPositiva(cantidad);

            /*  Verificar Stock */

            for (int i=0; i < cantidad; i++){
                if (!isGranjaExpirada(farm, cantidad)){
                    updateChickenStatus(listChicken, farm);
                    removeDeadChickens(listChicken, farm);
                }else {
                    saveListEgg(listEgg);
                    saveListChicken(listChicken);
                    farm.setDias(0);
                    farmRepository.save(farm);
                    throw new RuntimeException("El dueño de la granja acaba de irse.");
                }
                eggService.diasEnConvertirseEnPollo(i, farm);
            }
            updateFarmData(farm, diasDeVidaGranja, cantidad);
            farmRepository.save(farm);

            return new ApiResponse<>(200, "Acaban de pasar " + cantidad + " días.", "success");
        }catch (Exception e){
            
            return new ApiResponse<>(500, e.getMessage(), "danger");
        }

    }
}
