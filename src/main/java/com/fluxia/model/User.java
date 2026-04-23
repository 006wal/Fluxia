package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    // Paramètres du cycle
    private Integer cycleLength = 28;         // Durée moyenne du cycle en jours
    private Integer periodLength = 5;          // Durée des règles en jours
    private LocalDate lastPeriodDate;          // Date des dernières règles

    // Informations personnelles
    private LocalDate birthDate;
    private String avatarColor = "#E8829A";

    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();
}
