/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "notes_sante")
@Data
public class NoteSante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateNote;
    private String humeur;
    private String symptome;
    private String notePrivee;

    @ManyToOne
    @JoinColumn(name = "utilisatrice_id")
    private Utilisatrice utilisatrice;
}