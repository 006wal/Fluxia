package com.fluxia.repository;

import com.fluxia.model.CycleLog;
import com.fluxia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CycleLogRepository extends JpaRepository<CycleLog, Long> {

    List<CycleLog> findByUserOrderByDateDesc(User user);

    List<CycleLog> findByUserAndDateBetweenOrderByDate(User user, LocalDate start, LocalDate end);

    Optional<CycleLog> findByUserAndDate(User user, LocalDate date);

    List<CycleLog> findByUserAndIsPeriodDayTrueOrderByDateDesc(User user);

    @Query("SELECT c FROM CycleLog c WHERE c.user = :user AND c.isPeriodDay = true AND c.date >= :since ORDER BY c.date ASC")
    List<CycleLog> findPeriodDaysSince(User user, LocalDate since);

    boolean existsByUserAndDate(User user, LocalDate date);
}
