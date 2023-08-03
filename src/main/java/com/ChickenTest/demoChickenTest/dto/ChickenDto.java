package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChickenDto {
    private Long id;
    private int diasDeVida = 300;
    private int diasParaPonerHuevos = 10;
    private double precio = 90;
}
