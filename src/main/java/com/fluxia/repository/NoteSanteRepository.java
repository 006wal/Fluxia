/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.fluxia.repository;

import com.fluxia.model.NoteSante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteSanteRepository extends JpaRepository<NoteSante, Long> {
    List<NoteSante> findByUtilisatriceId(Long userId);
}