package com.ChickenTest.demoChickenTest.controller;

import com.ChickenTest.demoChickenTest.service.impl.FarmApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/farm")
public class FarmController {
    @Autowired
    FarmApiService farmApiService;

    @GetMapping
    public ResponseEntity<?> getDataFarmDto(){
        return new ResponseEntity<>(farmApiService.getDataFarmDto(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDataFarmDtoById(@PathVariable Long id){
        return new ResponseEntity<>(farmApiService.getDataFarmDtoById(id), HttpStatus.OK);
    }
    @GetMapping("/dashboard/resume")
    public ResponseEntity<?> getDataDashboardResume(){
        return new ResponseEntity<>(farmApiService.getDashboardResumen(), HttpStatus.OK);
    }

    @GetMapping("/dashboard/progress")
    public ResponseEntity<?> getDashboardProgress(){
        return new ResponseEntity<>(farmApiService.getDashboardProgress(), HttpStatus.OK);
    }

    @GetMapping("dashboard/transactions")
    public ResponseEntity<?> getDashboardBuySellProducts(){
        return new ResponseEntity<>(farmApiService.getDashboardBuySell(), HttpStatus.OK);
    }

    @GetMapping("dashboard/cash-available")
    public ResponseEntity<?> getCashAvailable(){
        return new ResponseEntity<>(farmApiService.getCashAvailable(), HttpStatus.OK);
    }
}
