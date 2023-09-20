package com.ChickenTest.demoChickenTest.controller;

import com.ChickenTest.demoChickenTest.service.impl.EggApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/egg")
public class EggController {
    @Autowired
    EggApiService eggApiService;

    @GetMapping
    public ResponseEntity<?> getEggData(){
        return new ResponseEntity<>(eggApiService.getDataEggsDto(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEggById(@PathVariable Long id){
        return new ResponseEntity<>(eggApiService.getDataEggsDtoById(id), HttpStatus.OK);
    }
}
