package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.FarmDashboardDto;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.ChickenTest.demoChickenTest.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class FarmServiceTest {
    @InjectMocks
    private FarmService farmService;
    @Mock
    private IFarmRepository farmRepository;
    @Mock
    private ObjectMapper mapper;
    @Mock//@Spy
    private FarmDashboardDto farmDashboardDto;
    private Farm farm;
    private List<Farm> listFarm = new ArrayList<>();
    @Before
    public void setUp() {
        farm = new Farm();
        List<Chicken> listChickens = new ArrayList<>();
        listChickens.add(new Chicken(1L, 20, 5, 50.0, null, farm));
        listChickens.add(new Chicken(2L, 15, 5, 50.0, null, farm));
        listChickens.add(new Chicken(3L, 15, 5, 50.0, null, farm));
        listChickens.add(new Chicken(4L, 20, 5, 50.0, null, farm));
        farm.setListChickens(listChickens);

        farm.setId(1L); // Establece un ID para la granja
        farm.setNombre("Farm App");
        listFarm.add(farm);

        // Configura el mock para que devuelva la granja cuando se llama a farmRepository.findById(1L)
        when(farmRepository.findById(1L)).thenReturn(Optional.of(farm));

        when(farmRepository.findAll()).thenReturn(listFarm);
        when(mapper.convertValue(farm, FarmDashboardDto.class)).thenReturn(farmDashboardDto);
    }

    @Test
    public void buy() {

        assertNotNull(farmService.getFarm(1L));
        assertEquals("Farm App",farm.getNombre());
        assertNotNull(farmService.getPropertiesDashboard());
        assertEquals(farmDashboardDto,farmService.getPropertiesDashboard());
        assertEquals(farmDashboardDto.getCantPollos(), farmService.getPropertiesDashboard().getCantPollos());
    }
}
