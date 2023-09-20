package com.ChickenTest.demoChickenTest.controller;

import com.ChickenTest.demoChickenTest.service.impl.ChickenApiService;
import com.ChickenTest.demoChickenTest.service.impl.ChickenService;
import com.ChickenTest.demoChickenTest.service.impl.EggService;
import com.ChickenTest.demoChickenTest.service.impl.FarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/chicken")
public class ChickenController {
    @Autowired
    ChickenApiService chickenApiService;

    @GetMapping()
    public ResponseEntity<?> getChickenData(){
        return new ResponseEntity<>(chickenApiService.getDataChickensDto(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getChickenById(@PathVariable Long id){
        return new ResponseEntity<>(chickenApiService.getDataChickensDtoById(id), HttpStatus.OK);
    }
}
