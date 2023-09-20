package com.ChickenTest.demoChickenTest.service.impl;

import com.ChickenTest.demoChickenTest.dto.ChickenDto;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.repository.IChickenRepository;
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
public class ChickenApiService {
    @Autowired
    IChickenRepository chickenRepository;
    @Autowired
    ObjectMapper mapper;

    /*  Reporte Chickens  */
    public List<ChickenDto> getDataChickensDto(){
        List<ChickenDto> listChickenDto = new ArrayList<>();

        for (Chicken chicken : chickenRepository.findAll()){
            ChickenDto chickenDto = mapper.convertValue(chicken, ChickenDto.class);
            listChickenDto.add(chickenDto);
        }

        return listChickenDto;
    }

    public ChickenDto getDataChickensDtoById(Long id){
        Chicken chicken = chickenRepository.findById(id).orElseThrow( () -> {
            return new RuntimeException("Chicken not found by Id: " + id);
        });

        return mapper.convertValue(chicken, ChickenDto.class);
    }
}
