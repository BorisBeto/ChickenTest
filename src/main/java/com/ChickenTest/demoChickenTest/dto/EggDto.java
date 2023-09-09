package com.ChickenTest.demoChickenTest.dto;

import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Farm;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EggDto {
    private Long id;
    private int diasEnConvertirseEnPollo;
    private double precio;
    private double precioComprado;
    private Chicken chicken;
    private Farm farm;
}
