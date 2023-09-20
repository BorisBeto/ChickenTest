package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.EggDto;
import com.ChickenTest.demoChickenTest.entity.Egg;
import com.ChickenTest.demoChickenTest.repository.IEggRepository;
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
public class EggApiService {
    @Autowired
    IEggRepository eggRepository;
    @Autowired
    ObjectMapper mapper;

    /*  Reporte Eggs    */
    public List<EggDto> getDataEggsDto(){
        List<EggDto> listEggDto = new ArrayList<>();

        for (Egg egg : eggRepository.findAll()){
            EggDto eggDto = mapper.convertValue(egg, EggDto.class);
            listEggDto.add(eggDto);
        }

        return listEggDto;
    }

    public EggDto getDataEggsDtoById(Long id){
        Egg egg = eggRepository.findById(id).orElseThrow( ()->{
            return new RuntimeException("Egg not found by Id: " + id);
        });

        return mapper.convertValue(egg, EggDto.class);
    }
}
