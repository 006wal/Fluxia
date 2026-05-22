/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */



package com.fluxia.service;

import com.fluxia.model.Article; // <--- CETTE LIGNE EST CRUCIALE
import com.fluxia.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> obtenirArticlesRecents() {
        return articleRepository.findTop3ByOrderByDatePublicationDesc();
    }

    public List<Article> obtenirTousLesArticles() {
        return articleRepository.findAll();
    }

    public Article publier(Article article) {
        return articleRepository.save(article);
    }

    public List<Article> filtrerParCategorie(String categorie) {
        return articleRepository.findByCategorie(categorie);
    }
}