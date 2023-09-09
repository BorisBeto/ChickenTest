package com.ChickenTest.demoChickenTest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;

import java.util.List;


@Entity
@Table(name = "GRANJAS")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String granjero;
    private double dinero;
    private double gastos;
    private int dias;
    private String fecha;
    private int limiteHuevos;
    private int limitePollos;
    private int cantHuevos;
    private int cantPollos;
    private int cantHuevosVendidos;
    private int cantPollosVendidos;

    @OneToMany(mappedBy = "farm")
    @JsonIgnore
    private List<Chicken> listChickens = new ArrayList<>();

    @OneToMany(mappedBy = "farm")
    @JsonIgnore
    private List<Egg> listEggs = new ArrayList<>();
}
