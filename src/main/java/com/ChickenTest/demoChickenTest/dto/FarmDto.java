package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FarmDto {
    private Long id;
    private String nombre = "Super Farm";
    private String granjero = "Brian Duran";
    private double dinero;
    private int dias = 300;
    private int limiteHuevos = 300;
    private int limitePollos = 300;
    //private int cantHuevos;
    //private int cantPollos;
}
