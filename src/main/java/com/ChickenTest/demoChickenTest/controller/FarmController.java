package com.ChickenTest.demoChickenTest.controller;

import com.ChickenTest.demoChickenTest.GlobalExceptionHandler;
import com.ChickenTest.demoChickenTest.dto.*;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.service.impl.ChickenService;
import com.ChickenTest.demoChickenTest.service.impl.EggService;
import com.ChickenTest.demoChickenTest.service.impl.FarmService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/")
public class FarmController {
    private static final Logger logger = Logger.getLogger(FarmController.class);
    @Autowired
    FarmService farmService;
    @Autowired
    ChickenService chickenService;
    @Autowired
    EggService eggService;

    @GetMapping
    public String getDashboardProperties(Model model){
        FarmDashboardDto farmDashboardDto = farmService.getPropertiesDashboard();

        model.addAttribute("farm", farmDashboardDto);

        logger.info("Farm properties: {}", farmDashboardDto);

        return "index";
    }

    @GetMapping("/farm")
    public String getDataTableFarm(Model model){
        FarmTableDto farmTableDto = farmService.getDataTableFarm();
        model.addAttribute("farmTable", farmTableDto);

        return "farmReport";
    }

    @GetMapping("/chicken")
    public String getDataTableChicken(Model model){
        List<ChickenDto> chickenDtos = chickenService.getDataTableChicken();
        model.addAttribute("chickenTable", chickenDtos);

        return "chickenReport";
    }
    @GetMapping("/egg")
    public String getDataTableEgg(Model model){
        List<EggDto> eggDtos = eggService.getDataTableEgg();
        model.addAttribute("eggTable", eggDtos);

        return "eggReport";
    }

    @PostMapping("/buy/chicken/{cantidad}")
    public String buyChicken(@PathVariable int cantidad){
        try {
            logger.info(farmService.getPropertiesDashboard());
            farmService.buy("chicken", cantidad);
        }catch (Exception e){
            logger.error("No se pudo realizar la compra: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/buy/egg/{cantidad}")
    public String buyEgg(@PathVariable int cantidad){

        try {
            logger.info(farmService.getPropertiesDashboard());
            farmService.buy("egg", cantidad);
        }catch (Exception e){
            logger.error("No se pudo realizar la compra: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/sell/chicken/{cantidad}")
    public String sellChicken(@PathVariable int cantidad){

        try {
            logger.info(farmService.getPropertiesDashboard());
            farmService.sell("chicken", cantidad);
        }catch (Exception e){
            logger.error("No se pudo realizar la venta: " + e.getMessage());
        }

        return "redirect:/";
    }
    @PostMapping("/sell/egg/{cantidad}")
    public String sellEgg(@PathVariable int cantidad){

        try {
            logger.info(farmService.getPropertiesDashboard());
            farmService.sell("egg", cantidad);
        }catch (Exception e){
            logger.error("No se pudo realizar la venta: " + e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/dias/{cantidad}")
    public String pasarDias(@PathVariable int cantidad){

        try {
            logger.info(farmService.getPropertiesDashboard());
            farmService.pasarDias(cantidad);
        }catch (Exception e){
            logger.error("Se ha producido un error: " + e.getMessage());
        }

        return "redirect:/";
    }
}
