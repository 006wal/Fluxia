/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.fluxia.controller;

import com.fluxia.model.Cycle;
import com.fluxia.model.Utilisatrice;
import com.fluxia.repository.CycleRepository;
import com.fluxia.repository.UtilisatriceRepository;
import com.fluxia.service.CycleService;
import com.fluxia.service.ArticleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class DashboardController {

    @Autowired
    private CycleService cycleService;

    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private CycleRepository cycleRepository;

    @Autowired
    private UtilisatriceRepository utilisatriceRepository;

    /**
     * 1. Affiche le formulaire de création de profil
     */
    @GetMapping("/setup-profile")
    public String afficherProfilForm() {
        return "setup-profile"; 
    }

    /**
     * 2. Sauvegarde le profil
     * CORRECTION : On ne force plus l'ID 1. On laisse MySQL générer l'ID.
     */
    @PostMapping("/save-profile")
    public String sauvegarderProfil(@RequestParam String nomComplet, 
                                    @RequestParam int age, 
                                    @RequestParam boolean aPartenaire,
                                    HttpSession session) {
        
        Utilisatrice user = new Utilisatrice();
        user.setNomComplet(nomComplet);
        user.setAge(age);
        user.setAPartenaire(aPartenaire);
        
        // Sauvegarde et récupération de l'objet avec son ID généré
        Utilisatrice savedUser = utilisatriceRepository.save(user);
        
        // On stocke l'ID réel en session pour les étapes suivantes
        session.setAttribute("userId", savedUser.getId());

        return "redirect:/setup-cycle";
    }

    /**
     * 3. Affiche le formulaire du cycle
     */
    @GetMapping("/setup-cycle")
    public String afficherCycleForm(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/setup-profile";
        }
        return "setup-cycle"; 
    }

    /**
     * 4. Sauvegarde le cycle
     */
    @PostMapping("/save-cycle")
    public String sauvegarderCycle(@RequestParam("dateDebut") String dateDebut, 
                                   @RequestParam("dureeCycle") int duree,
                                   HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/setup-profile";
        
        Utilisatrice user = utilisatriceRepository.findById(userId).orElseThrow();

        Cycle cycle = new Cycle();
        cycle.setUtilisatrice(user); // Lie le cycle à l'utilisatrice via la relation @ManyToOne
       // cycle.setUserId(userId);     // Garde aussi le userId simple si nécessaire
        cycle.setDateDernieresRegles(LocalDate.parse(dateDebut));
        cycle.setDureeCycle(duree);
        
        cycleRepository.save(cycle);
        return "redirect:/dashboard";
    }

    /**
     * 5. Page principale (Dashboard)
     */
    @GetMapping({"/", "/dashboard"})
    public String afficherDashboard(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        // Sécurité : Si pas d'ID en session, retour au début
        if (userId == null) {
            return "redirect:/setup-profile";
        }

        Utilisatrice user = utilisatriceRepository.findById(userId).orElse(null);
        Cycle cycle = cycleRepository.findByUserId(userId);

        if (user == null) return "redirect:/setup-profile";
        if (cycle == null) return "redirect:/setup-cycle";

        // Calculs et affichage
        int jour = cycleService.obtenirJourActuel(userId);
        
        model.addAttribute("nomUtilisatrice", user.getNomComplet());
        model.addAttribute("aPartenaire", user.isAPartenaire());
        model.addAttribute("jourActuel", jour);
        model.addAttribute("phase", cycleService.obtenirPhase(jour));
        model.addAttribute("conseil", cycleService.obtenirConseil(jour));
        model.addAttribute("joursRestants", cycleService.joursAvantRegles(userId, jour));
        model.addAttribute("articles", articleService.obtenirArticlesRecents());

        return "index"; 
    }
}