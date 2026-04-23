package com.fluxia.repository;

import com.fluxia.model.Reminder;
import com.fluxia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser(User user);
    List<Reminder> findByUserAndEnabled(User user, boolean enabled);
}
