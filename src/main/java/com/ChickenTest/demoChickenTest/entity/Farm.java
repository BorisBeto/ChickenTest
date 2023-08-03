package com.ChickenTest.demoChickenTest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

//import javax.persistence.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private int dias;
    private int limiteHuevos;
    private int limitePollos;
    private int cantHuevos;
    private int cantPollos;

    @OneToMany(mappedBy = "farm")
    @JsonIgnore
    private Set<Chicken> listChickens = new HashSet<>();

    @OneToMany(mappedBy = "farm")
    @JsonIgnore
    private Set<Egg> listEggs = new HashSet<>();
}
