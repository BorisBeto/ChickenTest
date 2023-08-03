package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FarmServiceTest {
    @Autowired
    IFarmRepository farmRepository;
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    IEggRepository eggRepository;

    @Autowired
    ObjectMapper mapper;


    @BeforeEach
    void setUp() {

        Farm farm = new Farm();
        farm.setNombre("SUPER FARM2");
        farm.setGranjero("Java Farm");
        farm.setDinero(5000);
        farm.setCantPollos(100);
        farm.setCantHuevos(100);

        farmRepository.save(farm);


    }

    @Test
    void getPropertiesDashboard() {
        List<Farm> listFarm = new ArrayList<>();

        listFarm.add(farmRepository.findAll().stream().findFirst().get());

        System.out.println(listFarm);
        System.out.println(farmRepository.findAll());

        Mockito.when(farmRepository.findAll()).thenReturn(listFarm);
    }

    @Test
    void buy() {
    }
}