package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.*;
import com.ChickenTest.demoChickenTest.entity.Farm;
import com.ChickenTest.demoChickenTest.repository.IFarmRepository;
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
public class FarmApiService {
    @Autowired
    IFarmRepository farmRepository;
    @Autowired
    FarmService farmService;
    @Autowired
    ChickenApiService chickenApiService;
    @Autowired
    EggApiService eggApiService;
    @Autowired
    ObjectMapper mapper;

    /*  Reporte Farm    */
    public List<FarmDto> getDataFarmDto(){
        List<FarmDto> listFarmDto = new ArrayList<>();

        for (Farm farm : farmRepository.findAll()){
            FarmDto farmDto = mapper.convertValue(farm, FarmDto.class);
            farmDto.setListChickens(chickenApiService.getDataChickensDto());
            farmDto.setListEggs(eggApiService.getDataEggsDto());
            listFarmDto.add(farmDto);
        }

        return listFarmDto;
    }

    public FarmDto getDataFarmDtoById(Long id){
        Farm farm = farmRepository.findById(id).orElseThrow( ()-> {
            return new RuntimeException("Farm not found by Id: " + id);
        });

        return mapper.convertValue(farm, FarmDto.class);
    }

    /*  Dashboard Properties    */
    public FarmDashboardDto getDashboardResumen(){
        return farmService.getPropertiesDashboard();
    }

    public FarmProgressDashboard getDashboardProgress(){
        return farmService.getPropertiesProgressDashboard();
    }

    public SectionBuySellProducts getDashboardBuySell(){
        return farmService.getSectionBuySellProducts();
    }

    public SectionCashAvailable getCashAvailable(){
        return farmService.getCashAvailable();
    }
}
