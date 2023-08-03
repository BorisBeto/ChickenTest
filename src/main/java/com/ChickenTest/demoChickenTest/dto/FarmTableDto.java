package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FarmTableDto {
    private Long id;
    private String nombre;
    private String granjero;
    private double dinero;
    private int dias;
    private int limiteHuevos;
    private int limitePollos;
    private int cantHuevos;
    private int cantPollos;
}
