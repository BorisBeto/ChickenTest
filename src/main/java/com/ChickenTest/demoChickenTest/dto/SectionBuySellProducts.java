package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SectionBuySellProducts {
    private int cantHuevos;
    private int cantPollos;
    private int cantHuevosVendidos;
    private int cantPollosVendidos;
    private double precioTotalHuevosComprados;
    private double precioTotalHuevosVendidos;
    private double precioTotalPollosComprados;
    private double precioTotalPollosVendidos;
}
