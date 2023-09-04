package com.ChickenTest.demoChickenTest.controller;

import com.ChickenTest.demoChickenTest.GlobalExceptionHandler;
import com.ChickenTest.demoChickenTest.dto.*;
import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Store;
import com.ChickenTest.demoChickenTest.response.ApiResponse;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    Store store;
    @GetMapping
    public String getDashboardProperties(Model model){
        FarmDashboardDto farmDashboardDto = farmService.getPropertiesDashboard();
        FarmProgressDashboard farmProgressDashboard = farmService.getPropertiesProgressDashboard();
        ChickenStatusDto chickenStatusDto = farmService.getCardChickenStatus();
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);
        model.addAttribute("cardChickenStatus", chickenStatusDto);
        model.addAttribute("progressFarm", farmProgressDashboard);
        model.addAttribute("farm", farmDashboardDto);
        model.addAttribute("store", store);

        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model){
        FarmProgressDashboard farmProgressDashboard = farmService.getPropertiesProgressDashboard();
        SectionCashAvailable sectionCashAvailable = farmService.getCashAvailable();
        SectionBuySellProducts sectionBuySellProducts = farmService.getSectionBuySellProducts();
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);
        model.addAttribute("cashAvailable", sectionCashAvailable);
        model.addAttribute("progressFarm", farmProgressDashboard);
        model.addAttribute("buySellProducts", sectionBuySellProducts);

        return "dashboard.html";
    }

    @GetMapping("/myAccount")
    public String myAccountPage(Model model){
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);
        return "myAccount.html";
    }

    @GetMapping("/editUsername")
    public String userNamePage(Model model){
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);

        return "editUsername.html";
    }
    @GetMapping("/editFarmName")
    public String farmNamePage(Model model) {
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);

        return "editFarmName.html";
    }

    @GetMapping("/editPrices")
    public String editPricesPage(Model model){
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);
        model.addAttribute("store", store);
        return "editPrices.html";
    }

    @GetMapping("/editPerfil")
    public String editPerfilPage(Model model){
        FarmTableDto farmTableDto = farmService.getDataTableFarm();

        model.addAttribute("farmApp", farmTableDto);

        return "editPerfil.html";
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

    @PostMapping("/edit/Username")
    public String updateUserName(@RequestParam String username, RedirectAttributes attributes){
        try {
            ApiResponse<String> response = farmService.updateUserName(username);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("Error al actualizar el nombre del granjero: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }
        return "redirect:/myAccount";
    }

    @PostMapping("/edit/FarmName")
    public String updateFarmName(@RequestParam String farmName, RedirectAttributes attributes){
        try {
            ApiResponse<String> response = farmService.updateFarmName(farmName);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("Error al actualizar el nombre de la granja: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }
        return "redirect:/myAccount";
    }

    @PostMapping("/edit/prices/{accion}/{tipo}")
    public String updatePrices(
            @PathVariable("accion") String accion,
            @PathVariable("tipo")   String tipo,
            @RequestParam double price,
            RedirectAttributes attributes){
        try {

            ApiResponse<String> response = farmService.updatePrices(accion,tipo,price);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("Error al actualizar el precio: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }
        return "redirect:/myAccount";
    }

    @PostMapping("/edit/perfil")
    public String updatePerfil(
            @RequestParam String userName,
            @RequestParam String farmName,
            @RequestParam int dayOfLife,
            @RequestParam int eggsCapacity,
            @RequestParam int chickensCapacity,
            RedirectAttributes attributes){

        try {

            ApiResponse<String> response = farmService.updatePerfil(userName, farmName, dayOfLife, eggsCapacity, chickensCapacity);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("Error al actualizar el perfil de usuario: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }
        return "redirect:/myAccount";
    }

    @PostMapping("/buy/chicken")
    public String buyChicken(@RequestParam int cantidad, RedirectAttributes attributes){
        try {
            ApiResponse<String> response = farmService.buy("chicken", cantidad);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("No se pudo realizar la compra: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/buy/egg")
    public String buyEgg(@RequestParam int cantidad, RedirectAttributes attributes){
        try {
            ApiResponse<String> response =  farmService.buy("egg", cantidad);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("No se pudo realizar la compra: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/sell/chicken")
    public String sellChicken(@RequestParam int cantidad, RedirectAttributes attributes){

        try {
            ApiResponse<String> response = farmService.sell("chicken", cantidad);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("No se pudo realizar la venta: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }

        return "redirect:/";
    }
    @PostMapping("/sell/egg")
    public String sellEgg(@RequestParam int cantidad, RedirectAttributes attributes){

        try {
            ApiResponse<String> response = farmService.sell("egg", cantidad);
            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("No se pudo realizar la venta: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }

        return "redirect:/";
    }

    @PostMapping("/dias")    //url:/dias/{cantidad}
    public String pasarDias(@RequestParam int cantidad, RedirectAttributes attributes){

        try {
            ApiResponse<String> response = farmService.pasarDias(cantidad);

            attributes.addFlashAttribute("apiResponse", response);
        }catch (Exception e){
            logger.error("Se ha producido un error: " + e.getMessage());
            attributes.addFlashAttribute("apiResponse", e.getMessage());
        }

        return "redirect:/";
    }



}
