package com.fluxia.service;

import com.fluxia.model.CycleLog;
import com.fluxia.model.User;
import com.fluxia.repository.CycleLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CycleCalculatorService {

    @Autowired
    private CycleLogRepository cycleLogRepository;

    /**
     * Calcule la date de début des prochaines règles
     */
    public LocalDate getNextPeriodDate(User user) {
        LocalDate lastPeriod = getLastPeriodStartDate(user);
        if (lastPeriod == null) {
            lastPeriod = user.getLastPeriodDate();
        }
        if (lastPeriod == null) return null;
        return lastPeriod.plusDays(user.getCycleLength());
    }

    /**
     * Retourne la date de début des dernières règles réelles
     */
    public LocalDate getLastPeriodStartDate(User user) {
        List<CycleLog> periodDays = cycleLogRepository.findByUserAndIsPeriodDayTrueOrderByDateDesc(user);
        if (periodDays.isEmpty()) return user.getLastPeriodDate();

        // Trouver le début du dernier cycle (premier jour consécutif)
        LocalDate lastDay = periodDays.get(0).getDate();
        for (CycleLog log : periodDays) {
            if (ChronoUnit.DAYS.between(log.getDate(), lastDay) > 3) break;
            lastDay = log.getDate();
        }
        return lastDay;
    }

    /**
     * Calcule la date d'ovulation (14 jours avant les prochaines règles)
     */
    public LocalDate getOvulationDate(User user) {
        LocalDate nextPeriod = getNextPeriodDate(user);
        if (nextPeriod == null) return null;
        return nextPeriod.minusDays(14);
    }

    /**
     * Calcule la fenêtre fertile (5 jours avant ovulation + jour ovulation + 1 jour après)
     */
    public Map<String, LocalDate> getFertileWindow(User user) {
        LocalDate ovulation = getOvulationDate(user);
        if (ovulation == null) return new HashMap<>();

        Map<String, LocalDate> window = new HashMap<>();
        window.put("start", ovulation.minusDays(5));
        window.put("ovulation", ovulation);
        window.put("end", ovulation.plusDays(1));
        return window;
    }

    /**
     * Détermine la phase actuelle du cycle
     */
    public CyclePhase getCurrentPhase(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastPeriod = getLastPeriodStartDate(user);
        if (lastPeriod == null) return CyclePhase.UNKNOWN;

        long dayOfCycle = ChronoUnit.DAYS.between(lastPeriod, today) + 1;
        int cycleLength = user.getCycleLength();
        int periodLength = user.getPeriodLength();

        // Normaliser si on est dans un nouveau cycle
        while (dayOfCycle > cycleLength) {
            dayOfCycle -= cycleLength;
        }

        LocalDate ovulation = getOvulationDate(user);
        if (ovulation == null) return CyclePhase.UNKNOWN;

        long daysToOvulation = ChronoUnit.DAYS.between(today, ovulation);
        long daysFromOvulation = ChronoUnit.DAYS.between(ovulation, today);

        if (dayOfCycle <= periodLength) {
            return CyclePhase.MENSTRUAL;
        } else if (dayOfCycle <= cycleLength / 2 - 2) {
            return CyclePhase.FOLLICULAR;
        } else if (daysToOvulation >= -1 && daysToOvulation <= 5) {
            return CyclePhase.OVULATION;
        } else {
            return CyclePhase.LUTEAL;
        }
    }

    /**
     * Nombre de jours actuel dans le cycle
     */
    public int getDayOfCycle(User user) {
        LocalDate lastPeriod = getLastPeriodStartDate(user);
        if (lastPeriod == null) {
            lastPeriod = user.getLastPeriodDate();
        }
        if (lastPeriod == null) return 0;
        long days = ChronoUnit.DAYS.between(lastPeriod, LocalDate.now()) + 1;
        int cycleLength = user.getCycleLength();
        // Normaliser
        int dayOfCycle = (int)(days % cycleLength);
        return dayOfCycle == 0 ? cycleLength : dayOfCycle;
    }

    /**
     * Jours restants avant les prochaines règles
     */
    public long getDaysUntilNextPeriod(User user) {
        LocalDate nextPeriod = getNextPeriodDate(user);
        if (nextPeriod == null) return -1;
        return ChronoUnit.DAYS.between(LocalDate.now(), nextPeriod);
    }

    /**
     * Calcule la durée moyenne du cycle basée sur l'historique
     */
    public OptionalDouble getAverageCycleLength(User user) {
        List<CycleLog> allPeriodDays = cycleLogRepository.findByUserAndIsPeriodDayTrueOrderByDateDesc(user);
        if (allPeriodDays.size() < 2) return OptionalDouble.empty();

        // Grouper par cycles
        List<LocalDate> cycleStarts = new ArrayList<>();
        LocalDate prev = allPeriodDays.get(0).getDate();
        cycleStarts.add(prev);

        for (int i = 1; i < allPeriodDays.size(); i++) {
            LocalDate current = allPeriodDays.get(i).getDate();
            if (ChronoUnit.DAYS.between(current, prev) > 5) {
                cycleStarts.add(current);
            }
            prev = current;
        }

        if (cycleStarts.size() < 2) return OptionalDouble.empty();

        List<Long> lengths = new ArrayList<>();
        for (int i = 0; i < cycleStarts.size() - 1; i++) {
            lengths.add(ChronoUnit.DAYS.between(cycleStarts.get(i + 1), cycleStarts.get(i)));
        }

        return lengths.stream().mapToLong(Long::longValue).average();
    }

    /**
     * Génère les données du calendrier pour un mois donné
     */
    public Map<String, String> getCalendarData(User user, int year, int month) {
        Map<String, String> calendarData = new HashMap<>();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        // Logs réels
        List<CycleLog> logs = cycleLogRepository.findByUserAndDateBetweenOrderByDate(user, firstDay, lastDay);
        for (CycleLog log : logs) {
            String key = log.getDate().toString();
            if (Boolean.TRUE.equals(log.getIsPeriodDay())) {
                calendarData.put(key, "period");
            } else if (log.getMood() != null || (log.getSymptoms() != null && !log.getSymptoms().isEmpty())) {
                calendarData.put(key, "logged");
            }
        }

        // Prédictions futures
        LocalDate nextPeriod = getNextPeriodDate(user);
        Map<String, LocalDate> fertileWindow = getFertileWindow(user);
        LocalDate ovulation = getOvulationDate(user);

        if (nextPeriod != null) {
            for (int i = 0; i < user.getPeriodLength(); i++) {
                LocalDate d = nextPeriod.plusDays(i);
                if (!d.isBefore(firstDay) && !d.isAfter(lastDay)) {
                    calendarData.putIfAbsent(d.toString(), "predicted_period");
                }
            }
        }

        if (ovulation != null && !ovulation.isBefore(firstDay) && !ovulation.isAfter(lastDay)) {
            calendarData.putIfAbsent(ovulation.toString(), "ovulation");
        }

        if (!fertileWindow.isEmpty()) {
            LocalDate fertileStart = fertileWindow.get("start");
            LocalDate fertileEnd = fertileWindow.get("end");
            if (fertileStart != null && fertileEnd != null) {
                LocalDate d = fertileStart;
                while (!d.isAfter(fertileEnd)) {
                    if (!d.isBefore(firstDay) && !d.isAfter(lastDay)) {
                        calendarData.putIfAbsent(d.toString(), "fertile");
                    }
                    d = d.plusDays(1);
                }
            }
        }

        return calendarData;
    }

    /**
     * Statistiques des symptômes sur les 3 derniers mois
     */
    public Map<String, Integer> getSymptomStats(User user) {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        List<CycleLog> logs = cycleLogRepository.findByUserAndDateBetweenOrderByDate(user, threeMonthsAgo, LocalDate.now());

        Map<String, Integer> stats = new TreeMap<>();
        for (CycleLog log : logs) {
            if (log.getSymptoms() != null) {
                for (String symptom : log.getSymptoms()) {
                    stats.merge(symptom, 1, Integer::sum);
                }
            }
        }
        return stats;
    }

    /**
     * Statistiques d'humeur
     */
    public Map<String, Integer> getMoodStats(User user) {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        List<CycleLog> logs = cycleLogRepository.findByUserAndDateBetweenOrderByDate(user, threeMonthsAgo, LocalDate.now());

        Map<String, Integer> stats = new TreeMap<>();
        for (CycleLog log : logs) {
            if (log.getMood() != null) {
                stats.merge(log.getMood().name(), 1, Integer::sum);
            }
        }
        return stats;
    }

    /**
     * Phases du cycle
     */
    public enum CyclePhase {
        MENSTRUAL("Menstruelle", "Tes règles sont là 🌸", "#E8829A"),
        FOLLICULAR("Folliculaire", "Énergie montante ✨", "#F4A261"),
        OVULATION("Ovulation", "Pic de fertilité 🌟", "#52B788"),
        LUTEAL("Lutéale", "Phase de repos 🌙", "#7B68EE"),
        UNKNOWN("Inconnue", "Configure ton cycle", "#999999");

        public final String label;
        public final String description;
        public final String color;

        CyclePhase(String label, String description, String color) {
            this.label = label;
            this.description = description;
            this.color = color;
        }
    }
}
