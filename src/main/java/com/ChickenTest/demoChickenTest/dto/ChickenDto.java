package com.ChickenTest.demoChickenTest.dto;

import com.ChickenTest.demoChickenTest.entity.Farm;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChickenDto {
    private Long id;
    private int diasDeVida;
    private int diasParaPonerHuevos;
    private double precio;
    private Farm farm;
}
