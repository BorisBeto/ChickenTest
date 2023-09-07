package com.ChickenTest.demoChickenTest.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "HUEVOS")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = {"farm"})
public class Egg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int diasEnConvertirseEnPollo;
    private double precio;

    @ManyToOne
    @JoinColumn(name = "chiken_id")
    private Chicken chicken;
    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
