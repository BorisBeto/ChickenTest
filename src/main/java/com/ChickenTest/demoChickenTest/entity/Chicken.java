package com.ChickenTest.demoChickenTest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//import javax.persistence.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Egg> listEggs = new HashSet<>(); // nroHuevosProducidos

    @ManyToOne
    @JoinColumn(name = "farm_id")
    private Farm farm;


}
