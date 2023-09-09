package com.ChickenTest.demoChickenTest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POLLOS")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString(exclude = {"farm"})
public class Chicken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int diasDeVida;
    private int diasParaPonerHuevos;
    private double precio;
    private double precioComprado;

    @OneToMany(mappedBy = "chicken")
    @JsonIgnore
    private List<Egg> listEggs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;
}
