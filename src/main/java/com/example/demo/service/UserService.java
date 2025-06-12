package com.example.demo.service;

import com.example.demo.entity.Agence;
import com.example.demo.entity.User;
import com.example.demo.repository.AgenceRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgenceRepository agenceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public User registerUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setScanAttmpt(0);
        user.setFaceAttampt(0);
        user.setSignature(null);
        user.setCarteType(null);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User saveUser(User user) {
        // If agence is set, make sure it's properly managed by the persistence context
        if (user.getAgence() != null && user.getAgence().getId() != null) {
            // Load the agence from database to ensure it's managed
            Optional<Agence> managedAgence = agenceRepository.findById(user.getAgence().getId());
            if (managedAgence.isPresent()) {
                user.setAgence(managedAgence.get());
            } else {
                throw new RuntimeException("Agence not found with ID: " + user.getAgence().getId());
            }
        }
        return userRepository.save(user);
    }

    @Transactional
    public User assignAgenceToUser(Long userId, Long agenceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (agenceId != null) {
            Agence agence = agenceRepository.findById(agenceId)
                    .orElseThrow(() -> new RuntimeException("Agence not found"));
            user.setAgence(agence);
        } else {
            user.setAgence(null);
        }

        User savedUser = userRepository.save(user);

        // Force refresh to ensure the relationship is properly saved
        entityManager.refresh(savedUser);

        return savedUser;
    }

    @Transactional
    public User assignAgenceToUser(User user, Agence agence) {
        user.setAgence(agence);
        User savedUser = userRepository.save(user);

        // Force refresh to ensure the relationship is properly saved
        entityManager.refresh(savedUser);

        return savedUser;
    }

    public User updateUserWithPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return saveUser(user); // Use the updated saveUser method
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public User updateSignatureAndCarteType(Long userId, String signature, String carteType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setSignature(signature);
        user.setCarteType(carteType);
        return userRepository.save(user);
    }

    @Transactional
    public User updateIsHasAccount(Long userId, boolean isHasAccount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setHasAccount(isHasAccount);
        return userRepository.save(user);
    }

    // Helper method to verify agence assignment
    public boolean isAgenceAssigned(Long userId, Long agenceId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent() && user.get().getAgence() != null) {
            return user.get().getAgence().getId().equals(agenceId);
        }
        return false;
    }

    // Method to get user with agence details
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithAgence(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent() && user.get().getAgence() != null) {
            // Force loading of agence to avoid lazy loading issues
            user.get().getAgence().getName(); // This triggers the lazy loading
        }
        return user;
    }
}