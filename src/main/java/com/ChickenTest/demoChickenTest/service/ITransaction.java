package com.ChickenTest.demoChickenTest.service;


import com.ChickenTest.demoChickenTest.dto.TransaccionDto;

public interface ITransaction {
    boolean buyProduct(TransaccionDto transaccionDto);
    void sellProduct(TransaccionDto transaccionDto);
}
