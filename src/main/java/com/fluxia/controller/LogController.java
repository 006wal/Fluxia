package com.fluxia.controller;

import com.fluxia.service.CycleLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class LogController {

    @Autowired
    private CycleLogService cycleLogService;

    @GetMapping("/log")
    public String logPage(HttpSession session, Model model,
                          @RequestParam(required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        LocalDate logDate = date != null ? date : LocalDate.now();
        model.addAttribute("logDate", logDate);
        model.addAttribute("existingLog", cycleLogService.getLogForDate(userId, logDate).orElse(null));
        return "log";
    }
}
