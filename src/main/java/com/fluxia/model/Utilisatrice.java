/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "utilisatrices")
@Data
public class Utilisatrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomComplet;
    private String email;
    private String motDePasse;
    
    private int age;
    private boolean aPartenaire;

    private int dureeCycleMoyen = 28;
    private int dureeReglesMoyenne = 5;

    // mappedBy fait référence au nom du champ "utilisatrice" dans la classe Cycle
    @OneToMany(mappedBy = "utilisatrice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cycle> cycles = new ArrayList<>();
}