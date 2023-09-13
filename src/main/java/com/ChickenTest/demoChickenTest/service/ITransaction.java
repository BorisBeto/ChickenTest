package com.ChickenTest.demoChickenTest.service;

import com.ChickenTest.demoChickenTest.entity.Farm;

public interface ITransaction {
    void buy(Farm farm, int cantidad);
    void sell(Farm farm, int cantidad);
    void sellExcedent(Farm farm, int news, int excedent);
}
