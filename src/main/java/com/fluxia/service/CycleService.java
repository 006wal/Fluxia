/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.fluxia.service;

import com.fluxia.model.Cycle;
import com.fluxia.repository.CycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CycleService {

    @Autowired
    private CycleRepository cycleRepository;

    /**
     * Calcule le jour actuel du cycle basé sur la date réelle en BDD.
     */
    public int obtenirJourActuel(Long userId) {
        // On récupère le cycle via l'userId (comme configuré dans le controller)
        Cycle cycle = cycleRepository.findByUserId(userId);
        
        if (cycle != null && cycle.getDateDernieresRegles() != null) {
            LocalDate dateDebut = cycle.getDateDernieresRegles();
            // Calcul de la différence réelle entre aujourd'hui et les dernières règles
            long jours = ChronoUnit.DAYS.between(dateDebut, LocalDate.now());
            
            // On ajoute 1 car le jour même des règles est le "Jour 1"
            int jourCalcule = (int) jours + 1;

            // Gestion de la boucle : si on dépasse la durée, on recommence (modulo)
            if (jourCalcule > cycle.getDureeCycle()) {
                return (jourCalcule % cycle.getDureeCycle() == 0) ? cycle.getDureeCycle() : (jourCalcule % cycle.getDureeCycle());
            }
            return jourCalcule;
        }
        return 1; // Par défaut si rien n'est trouvé
    }

    /**
     * Détermine la phase actuelle.
     */
    public String obtenirPhase(int jour) {
        if (jour <= 5) return "Phase Menstruelle";
        if (jour >= 6 && jour <= 12) return "Phase Folliculaire";
        if (jour >= 13 && jour <= 16) return "Phase d'Ovulation";
        return "Phase Lutéale";
    }

    /**
     * Conseil dynamique pour le Dashboard.
     */
    public String obtenirConseil(int jour) {
        if (jour <= 5) return "Reposez-vous et privilégiez les aliments riches en fer (épinards, lentilles).";
        if (jour >= 13 && jour <= 16) return "Votre énergie est au maximum ! C'est le moment idéal pour le sport.";
        if (jour > 20) return "Réduisez le sel et buvez beaucoup d'eau pour éviter les ballonnements.";
        return "Maintenez une activité physique douce et dormez suffisamment.";
    }

    /**
     * Calcule le compte à rebours avant les prochaines règles.
     */
    public int joursAvantRegles(Long userId, int jourActuel) {
        Cycle cycle = cycleRepository.findByUserId(userId);
        int cycleMoyen = (cycle != null) ? cycle.getDureeCycle() : 28;
        
        int restant = cycleMoyen - jourActuel;
        return Math.max(restant, 0);
    }
}