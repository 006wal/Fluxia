package com.fluxia.controller;

import com.fluxia.model.User;
import com.fluxia.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = userService.login(email, password)
                    .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
        try {
            User user = userService.createUser(name, email, password);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            return "redirect:/onboarding";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/onboarding")
    public String onboarding(HttpSession session) {
        if (session.getAttribute("userId") == null) return "redirect:/login";
        return "onboarding";
    }

    @PostMapping("/onboarding")
    public String saveOnboarding(@RequestParam Integer cycleLength,
                                 @RequestParam Integer periodLength,
                                 @RequestParam String lastPeriodDate,
                                 HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        userService.updateCycleSettings(userId, cycleLength, periodLength,
                java.time.LocalDate.parse(lastPeriodDate));
        return "redirect:/dashboard";
    }
}
