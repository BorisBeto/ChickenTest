package com.ChickenTest.demoChickenTest.service;

import com.ChickenTest.demoChickenTest.dto.FarmDashboardDto;
import com.ChickenTest.demoChickenTest.dto.FarmDto;

public interface IFarmService {
    int verificarStockChicken(Long id);
    int verificarStockEgg(Long id);
    FarmDashboardDto getInfoGeneral(Long id);
}
