package com.ChickenTest.demoChickenTest.service.impl;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.ChickenTest.demoChickenTest.dto.ChickenDto;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.entity.LifeCycle;
import com.ChickenTest.demoChickenTest.entity.Store;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class ChickenServiceTest {
    @InjectMocks
    private ChickenService chickenService;
    @Mock
    private IChickenRepository chickenRepository;
    @Mock
    private IFarmRepository farmRepository;
    @Mock
    private ObjectMapper mapper;
    private Farm farm;
    private List<Chicken> listChickens = new ArrayList<>();
    private Chicken chicken;

    @BeforeEach
    void setUp() {
        farm = new Farm(1L, "Farm App", "Brian Duran", 500.0, 0.0, 100, 20, 20, 0, 0,0,0,null, null);

    }

    private List<Chicken> createChickens(){
        // Crea una lista de pollos de ejemplo
        return Arrays.asList(
                new Chicken(1L, 20, 5, 100.0, null, farm),
                new Chicken(2L, 20, 5, 100.0, null, farm),
                new Chicken(3L, 20, 5, 100.0, null, farm),
                new Chicken(4L, 20, 5, 100.0, null, farm),
                new Chicken(5L, 20, 5, 100.0, null, farm)
        );
    }
    @Test
    void getDataTableChickenWithData() {
        List<Chicken> listChickens = createChickens();

        when(chickenRepository.findAll()).thenReturn(listChickens);

        for (Chicken chicken : listChickens) {
            when(mapper.convertValue(chicken, ChickenDto.class)).thenReturn(new ChickenDto(chicken.getId(), chicken.getDiasDeVida(), chicken.getDiasParaPonerHuevos(), chicken.getPrecio(), farm));
        }

        List<ChickenDto> result = chickenService.getDataTableChicken();

        assertNotNull(result);
        assertEquals(listChickens.size(), result.size());

        //  Comparando mi lisa de ChickenDto contra mi lista de Chicken.
        for (int i = 0; i < listChickens.size(); i++) {
            Chicken expectedChicken = listChickens.get(i);
            ChickenDto actualChickenDto = result.get(i);

            assertEquals(expectedChicken.getId(), actualChickenDto.getId());
            assertEquals(expectedChicken.getDiasParaPonerHuevos(), actualChickenDto.getDiasParaPonerHuevos());
            assertEquals(expectedChicken.getPrecio(), actualChickenDto.getPrecio());
            assertEquals(expectedChicken.getFarm(), actualChickenDto.getFarm());
        }
    }
    @Test
    void getDataTableChickenWhitoutData() {
        // Configura el comportamiento del chickenRepository para que devuelva una lista vacía
        when(chickenRepository.findAll()).thenReturn(new ArrayList<>());

        // Llamo al método que quiero probar
        List<ChickenDto> result = chickenService.getDataTableChicken();

        // Verifica que el resultado sea una lista vacía
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void verifyStock() {
        Throwable exception = assertThrows(RuntimeException.class, () -> chickenService.verifyStock(5, 20, 15));

        // Verifica que el método lance la excepción esperada
        assertEquals("Supero la cantidad Máxima de Pollos.", exception.getMessage());
    }
    @Test
    void isLimiteStock() {
        /*Debe ser True, cuando supera el Limite de Stock. Caso contrario debe dar False*/
        assertFalse(chickenService.isLimiteStock(5, 1, 10), "No debe superar limite de Stock.");
        assertFalse(chickenService.isLimiteStock(-5, -10, 10), "No debe superar limite de Stock.");
        assertFalse(chickenService.isLimiteStock(5, 5, 10), "No debe superar limite de Stock.");

        assertTrue(chickenService.isLimiteStock(5, 5, 5), "Debe superar limite de Stock.");
        assertTrue(chickenService.isLimiteStock(15, 5, 10), "Debe superar limite de Stock.");
        assertTrue(chickenService.isLimiteStock(1, 10, 10), "Debe superar limite de Stock.");
    }
    @ParameterizedTest
    @MethodSource("dataProvider")
    void buy(int amountOfChicken, double expectedCashAvailable) {
        try{
            for (int i = 0; i < amountOfChicken; i++){
                chicken = new Chicken((long) (i + 1), LifeCycle.DAY_OF_LIFE_CHICKEN, LifeCycle.DAY_TO_LAY_EGGS, Store.PRECIO_COMPRA_CHICKEN, null, farm);
                when(chickenRepository.save(chicken)).thenReturn(chicken);
                listChickens.add(chicken);
            }

            farm.setListChickens(listChickens);
            chickenService.buy(farm, amountOfChicken);

            assertEquals(amountOfChicken, farm.getCantPollos());
            assertEquals(amountOfChicken,farm.getListChickens().size());
            assertEquals(expectedCashAvailable, farm.getDinero());

        }catch (Exception e){
            if (e.getMessage().equals("La cantidad ingresada debe ser Entero positivo.")){
                assertEquals(expectedCashAvailable, farm.getDinero());
            } else if (e.getMessage().equals("Supero la cantidad Máxima de Pollos.")) {
                assertTrue(chickenService.isLimiteStock(amountOfChicken, farm.getCantPollos(), farm.getLimitePollos()));
                assertEquals(expectedCashAvailable, farm.getDinero());
            }else if (e.getMessage().equals("Dinero disponible insuficiente.")){
                assertFalse(expectedCashAvailable > farm.getDinero());
            }
        }
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                /*  Verificando que la cantidad a comprar sea positiva. */
                Arguments.of(-22, 500),
                Arguments.of(0, 500),
                /*  Comprobando la compra exitosa. Sin superar el limite de Stock */
                Arguments.of(1, 500 - (1 * 90)),
                Arguments.of(3, 500 - (3 * 90)),
                /*  Verificando que tenga saldo suficiente para comprar.*/
                Arguments.of(15, 500 - (15 * 90)),
                /*  Verificando que la cantidd a comprar no supere el Stock de Pollos.*/
                Arguments.of(22, 500),
                Arguments.of(315, 500)
        );
    }

    @Test
    void sell() {
    }

    @Test
    void sellExcedent() {
    }
}