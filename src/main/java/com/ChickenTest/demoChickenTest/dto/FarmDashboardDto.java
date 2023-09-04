package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class FarmDashboardDto extends Throwable {
    private Long id;
    private double dinero;
    private int cantHuevos;   //cantidad
    private int cantPollos;       //cantidad
    private int limiteHuevos;
    private int limitePollos;
    private String fecha;
}
