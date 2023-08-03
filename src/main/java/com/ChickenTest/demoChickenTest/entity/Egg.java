package com.ChickenTest.demoChickenTest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Table(name = "HUEVOS")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Egg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int diasEnConvertirseEnPollo = 5;
    private double precio = 50;

    @ManyToOne
    @JoinColumn(name = "chiken_id")
    private Chicken chicken;
    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
