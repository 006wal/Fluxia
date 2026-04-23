package com.fluxia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ReminderType type;

    private Boolean enabled = true;
    private String time = "08:00"; // Format HH:mm
    private Integer daysBefore = 1; // Pour période et ovulation

    public enum ReminderType {
        PERIOD_REMINDER,       // Rappel des prochaines règles
        OVULATION_REMINDER,    // Rappel de l'ovulation
        LOG_REMINDER,          // Rappel de journalisation quotidienne
        PILL_REMINDER,         // Rappel pilule contraceptive
        FERTILE_WINDOW         // Fenêtre fertile
    }
}
