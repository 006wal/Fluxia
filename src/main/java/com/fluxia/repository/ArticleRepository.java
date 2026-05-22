/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.fluxia.repository;

import com.fluxia.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Permet de filtrer par catégorie (ex: voir tous les articles "Nutrition")
    List<Article> findByCategorie(String categorie);
    
    // Récupérer les derniers articles publiés
    List<Article> findTop3ByOrderByDatePublicationDesc();
}