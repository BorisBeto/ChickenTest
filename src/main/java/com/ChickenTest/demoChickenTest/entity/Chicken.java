package com.ChickenTest.demoChickenTest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POLLOS")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Chicken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int diasDeVida;
    private int diasParaPonerHuevos;
    private double precio;

    @OneToMany(mappedBy = "chicken")
    @JsonIgnore
    private List<Egg> listEggs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;


}
