package com.fluxia.service;

import com.fluxia.model.CycleLog;
import com.fluxia.model.User;
import com.fluxia.repository.CycleLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CycleLogService {

    @Autowired
    private CycleLogRepository cycleLogRepository;

    @Autowired
    private UserService userService;

    public CycleLog saveOrUpdateLog(Long userId, LocalDate date, CycleLog logData) {
        User user = userService.getUserById(userId);

        CycleLog log = cycleLogRepository.findByUserAndDate(user, date)
                .orElse(new CycleLog());

        log.setUser(user);
        log.setDate(date);

        if (logData.getIsPeriodDay() != null) log.setIsPeriodDay(logData.getIsPeriodDay());
        if (logData.getFlowIntensity() != null) log.setFlowIntensity(logData.getFlowIntensity());
        if (logData.getSymptoms() != null) log.setSymptoms(logData.getSymptoms());
        if (logData.getMood() != null) log.setMood(logData.getMood());
        if (logData.getBasalTemperature() != null) log.setBasalTemperature(logData.getBasalTemperature());
        if (logData.getCervicalMucus() != null) log.setCervicalMucus(logData.getCervicalMucus());
        if (logData.getSexualActivity() != null) log.setSexualActivity(logData.getSexualActivity());
        if (logData.getUsedContraception() != null) log.setUsedContraception(logData.getUsedContraception());
        if (logData.getNotes() != null) log.setNotes(logData.getNotes());

        // Si c'est un jour de règles, mettre à jour la dernière date de règles de l'utilisateur
        if (Boolean.TRUE.equals(log.getIsPeriodDay())) {
            User u = userService.getUserById(userId);
            if (u.getLastPeriodDate() == null || date.isAfter(u.getLastPeriodDate())) {
                u.setLastPeriodDate(date);
                userService.save(u);
            }
        }

        return cycleLogRepository.save(log);
    }

    public Optional<CycleLog> getLogForDate(Long userId, LocalDate date) {
        User user = userService.getUserById(userId);
        return cycleLogRepository.findByUserAndDate(user, date);
    }

    public List<CycleLog> getLogsForMonth(Long userId, int year, int month) {
        User user = userService.getUserById(userId);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return cycleLogRepository.findByUserAndDateBetweenOrderByDate(user, start, end);
    }

    public List<CycleLog> getRecentLogs(Long userId, int days) {
        User user = userService.getUserById(userId);
        LocalDate since = LocalDate.now().minusDays(days);
        return cycleLogRepository.findByUserAndDateBetweenOrderByDate(user, since, LocalDate.now());
    }

    public void deleteLog(Long userId, LocalDate date) {
        User user = userService.getUserById(userId);
        cycleLogRepository.findByUserAndDate(user, date)
                .ifPresent(cycleLogRepository::delete);
    }
}
