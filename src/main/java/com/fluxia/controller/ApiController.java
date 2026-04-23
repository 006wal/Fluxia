package com.fluxia.controller;

import com.fluxia.model.CycleLog;
import com.fluxia.model.User;
import com.fluxia.service.CycleCalculatorService;
import com.fluxia.service.CycleLogService;
import com.fluxia.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired private CycleLogService cycleLogService;
    @Autowired private UserService userService;
    @Autowired private CycleCalculatorService cycleCalculator;

    // === LOGS ===

    @PostMapping("/logs/{date}")
    public ResponseEntity<?> saveLog(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                     @RequestBody CycleLog logData,
                                     HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        CycleLog saved = cycleLogService.saveOrUpdateLog(userId, date, logData);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/logs/{date}")
    public ResponseEntity<?> getLog(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                    HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        Optional<CycleLog> log = cycleLogService.getLogForDate(userId, date);
        return ResponseEntity.ok(log.orElse(null));
    }

    @DeleteMapping("/logs/{date}")
    public ResponseEntity<?> deleteLog(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        cycleLogService.deleteLog(userId, date);
        return ResponseEntity.ok().build();
    }

    // === CALENDRIER ===

    @GetMapping("/calendar/{year}/{month}")
    public ResponseEntity<?> getCalendarData(@PathVariable int year,
                                              @PathVariable int month,
                                              HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        User user = userService.getUserById(userId);
        Map<String, String> data = cycleCalculator.getCalendarData(user, year, month);
        return ResponseEntity.ok(data);
    }

    // === PRÉDICTIONS ===

    @GetMapping("/predictions")
    public ResponseEntity<?> getPredictions(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        User user = userService.getUserById(userId);
        Map<String, Object> predictions = new HashMap<>();
        predictions.put("nextPeriod", cycleCalculator.getNextPeriodDate(user));
        predictions.put("ovulation", cycleCalculator.getOvulationDate(user));
        predictions.put("fertileWindow", cycleCalculator.getFertileWindow(user));
        predictions.put("dayOfCycle", cycleCalculator.getDayOfCycle(user));
        predictions.put("daysUntilPeriod", cycleCalculator.getDaysUntilNextPeriod(user));
        predictions.put("phase", cycleCalculator.getCurrentPhase(user));

        return ResponseEntity.ok(predictions);
    }

    // === PROFIL ===

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> body,
                                            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        String name = (String) body.get("name");
        String birthDateStr = (String) body.get("birthDate");
        String avatarColor = (String) body.get("avatarColor");

        LocalDate birthDate = birthDateStr != null ? LocalDate.parse(birthDateStr) : null;
        User updated = userService.updateProfile(userId, name, birthDate, avatarColor);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/cycle-settings")
    public ResponseEntity<?> updateCycleSettings(@RequestBody Map<String, Object> body,
                                                   HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).body("Non connecté");

        Integer cycleLength = body.get("cycleLength") != null ? (Integer) body.get("cycleLength") : null;
        Integer periodLength = body.get("periodLength") != null ? (Integer) body.get("periodLength") : null;
        String lastPeriodStr = (String) body.get("lastPeriodDate");
        LocalDate lastPeriodDate = lastPeriodStr != null ? LocalDate.parse(lastPeriodStr) : null;

        User updated = userService.updateCycleSettings(userId, cycleLength, periodLength, lastPeriodDate);
        return ResponseEntity.ok(updated);
    }
}
