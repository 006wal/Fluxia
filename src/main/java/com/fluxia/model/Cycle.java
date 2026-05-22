/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "cycles")
@Data
public class Cycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDernieresRegles; 
    private int dureeCycle; 
    private boolean estTermine = false;

    // La relation "Many Cycles to One Utilisatrice"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisatrice_id", nullable = false)
    private Utilisatrice utilisatrice;

    // Méthode utilitaire pour obtenir l'ID sans casser ton code actuel
    public Long getUserId() {
        return (utilisatrice != null) ? utilisatrice.getId() : null;
    }
}