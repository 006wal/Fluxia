package com.fluxia.controller;

import com.fluxia.model.User;
import com.fluxia.service.CycleCalculatorService;
import com.fluxia.service.CycleLogService;
import com.fluxia.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired private UserService userService;
    @Autowired private CycleCalculatorService cycleCalculator;
    @Autowired private CycleLogService cycleLogService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        CycleCalculatorService.CyclePhase phase = cycleCalculator.getCurrentPhase(user);

        model.addAttribute("user", user);
        model.addAttribute("phase", phase);
        model.addAttribute("dayOfCycle", cycleCalculator.getDayOfCycle(user));
        model.addAttribute("daysUntilPeriod", cycleCalculator.getDaysUntilNextPeriod(user));
        model.addAttribute("nextPeriodDate", cycleCalculator.getNextPeriodDate(user));
        model.addAttribute("ovulationDate", cycleCalculator.getOvulationDate(user));
        model.addAttribute("fertileWindow", cycleCalculator.getFertileWindow(user));
        model.addAttribute("todayLog", cycleLogService.getLogForDate(userId, LocalDate.now()).orElse(null));
        model.addAttribute("today", LocalDate.now());

        // Calendrier du mois courant
        LocalDate now = LocalDate.now();
        Map<String, String> calendarData = cycleCalculator.getCalendarData(user, now.getYear(), now.getMonthValue());
        model.addAttribute("calendarData", calendarData);
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("currentMonth", now.getMonthValue());

        return "dashboard";
    }

    @GetMapping("/calendar")
    public String calendar(HttpSession session, Model model,
                           @org.springframework.web.bind.annotation.RequestParam(required = false) Integer year,
                           @org.springframework.web.bind.annotation.RequestParam(required = false) Integer month) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        LocalDate now = LocalDate.now();
        int y = year != null ? year : now.getYear();
        int m = month != null ? month : now.getMonthValue();

        model.addAttribute("user", user);
        model.addAttribute("calendarData", cycleCalculator.getCalendarData(user, y, m));
        model.addAttribute("currentYear", y);
        model.addAttribute("currentMonth", m);
        model.addAttribute("today", now);

        return "calendar";
    }

    @GetMapping("/insights")
    public String insights(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);

        model.addAttribute("user", user);
        model.addAttribute("symptomStats", cycleCalculator.getSymptomStats(user));
        model.addAttribute("moodStats", cycleCalculator.getMoodStats(user));
        model.addAttribute("avgCycleLength", cycleCalculator.getAverageCycleLength(user));
        model.addAttribute("recentLogs", cycleLogService.getRecentLogs(userId, 90));

        return "insights";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "profile";
    }
}
