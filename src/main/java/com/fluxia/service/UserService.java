package com.fluxia.service;

import com.fluxia.model.User;
import com.fluxia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // En production, utiliser BCrypt
        user.setCreatedAt(LocalDate.now());
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(u -> password.equals(u.getPassword()));
    }

    public User updateCycleSettings(Long userId, Integer cycleLength, Integer periodLength, LocalDate lastPeriodDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (cycleLength != null) user.setCycleLength(cycleLength);
        if (periodLength != null) user.setPeriodLength(periodLength);
        if (lastPeriodDate != null) user.setLastPeriodDate(lastPeriodDate);
        return userRepository.save(user);
    }

    public User updateProfile(Long userId, String name, LocalDate birthDate, String avatarColor) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (name != null) user.setName(name);
        if (birthDate != null) user.setBirthDate(birthDate);
        if (avatarColor != null) user.setAvatarColor(avatarColor);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
