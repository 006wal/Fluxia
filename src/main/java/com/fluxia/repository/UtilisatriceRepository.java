/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package com.fluxia.repository;

import com.fluxia.model.Utilisatrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisatriceRepository extends JpaRepository<Utilisatrice, Long> {
    
    // Pour la future connexion
    Utilisatrice findByEmail(String email);
    
    // Optionnel mais propre : pour récupérer l'utilisatrice par son ID
    // JpaRepository possède déjà findById, donc rien à ajouter de spécial ici !
}