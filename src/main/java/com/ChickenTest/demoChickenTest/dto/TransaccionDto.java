package com.ChickenTest.demoChickenTest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TransaccionDto {
    private Long id;
    private int cantidad;
    private String tipo;
}
