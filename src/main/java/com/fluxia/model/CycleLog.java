package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cycle_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    // === RÈGLES ===
    private Boolean isPeriodDay = false;

    @Enumerated(EnumType.STRING)
    private FlowIntensity flowIntensity; // LIGHT, MEDIUM, HEAVY, SPOTTING

    // === SYMPTÔMES ===
    @ElementCollection
    @CollectionTable(name = "log_symptoms", joinColumns = @JoinColumn(name = "log_id"))
    @Column(name = "symptom")
    private List<String> symptoms; // cramps, headache, bloating, fatigue, breast_tenderness, acne, nausea, back_pain

    // === HUMEUR ===
    @Enumerated(EnumType.STRING)
    private Mood mood; // HAPPY, SAD, ANXIOUS, IRRITATED, CALM, ENERGETIC, SENSITIVE, TIRED

    // === TEMPÉRATURE BASALE ===
    private Double basalTemperature;

    // === GLAIRE CERVICALE ===
    @Enumerated(EnumType.STRING)
    private CervicalMucus cervicalMucus; // DRY, STICKY, CREAMY, WATERY, EGG_WHITE

    // === ACTIVITÉ SEXUELLE ===
    private Boolean sexualActivity = false;
    private Boolean usedContraception = false;

    // === NOTES ===
    @Column(length = 1000)
    private String notes;

    // === ÉNUMS ===
    public enum FlowIntensity {
        SPOTTING, LIGHT, MEDIUM, HEAVY
    }

    public enum Mood {
        HAPPY, SAD, ANXIOUS, IRRITATED, CALM, ENERGETIC, SENSITIVE, TIRED
    }

    public enum CervicalMucus {
        DRY, STICKY, CREAMY, WATERY, EGG_WHITE
    }
}
