/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.fluxia.repository;

import com.fluxia.model.Cycle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CycleRepository extends JpaRepository<Cycle, Long> {
    
    // Pour le DashboardController (version simple avec Long userId)
    Cycle findByUserId(Long userId);

    // Ta méthode initiale (utile quand tu auras Spring Security)
    Optional<Cycle> findByUtilisatriceIdAndEstTermineFalse(Long utilisatriceId);
}