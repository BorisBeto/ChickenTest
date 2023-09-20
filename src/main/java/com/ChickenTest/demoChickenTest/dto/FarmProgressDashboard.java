package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class FarmProgressDashboard{ // extends Throwable
    private Long id;
    private int diasVida;
    private int diasTranscurridos;
    private int cantHuevos;
    private int cantPollos;
    private int limiteHuevos;
    private int limitePollos;
    private double porcentajeDiasVida;
    private double porcentajePollos;
    private double porcentajeHuevos;
}
