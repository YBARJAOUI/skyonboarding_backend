package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private Step1Service step1Service;

    @Autowired
    private Step2Service step2Service;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private AgenceService agenceService;

    @Autowired
    private RendezvousService rendezvousService;

    // User management endpoints for admin
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        try {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "code", "001",
                        "label", "Username already exists"
                ));
            }

            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            User savedUser = userService.registerUser(user);
            return ResponseEntity.ok(Map.of(
                    "code", "000",
                    "label", "User created successfully",
                    "user", savedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "code", "500",
                    "label", "Error creating user: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Update user fields
                if (userDetails.getNationality() != null) {
                    user.setNationality(userDetails.getNationality());
                }
                if (userDetails.getSkyOrigin() != null) {
                    user.setSkyOrigin(userDetails.getSkyOrigin());
                }
                if (userDetails.getRole() != null) {
                    user.setRole(userDetails.getRole());
                }
                user.setUsernameEmail(userDetails.isUsernameEmail());

                // Handle agency assignment
                if (userDetails.getAgenceId() != null) {
                    Optional<Agence> agence = agenceService.findById(userDetails.getAgenceId());
                    if (agence.isPresent()) {
                        user.setAgence(agence.get());
                    }
                }

                User savedUser;

                // Update password if provided
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    user.setPassword(userDetails.getPassword());
                    savedUser = userService.updateUserWithPassword(user);
                } else {
                    savedUser = userService.saveUser(user);
                }

                return ResponseEntity.ok(Map.of(
                        "code", "000",
                        "label", "User updated successfully",
                        "user", savedUser
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "code", "404",
                        "label", "User not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "code", "500",
                    "label", "Error updating user: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/users/{userId}/assign-agency/{agencyId}")
    public ResponseEntity<Map<String, Object>> assignAgencyToUser(
            @PathVariable Long userId,
            @PathVariable Long agencyId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Agence agence = agenceService.findById(agencyId)
                    .orElseThrow(() -> new RuntimeException("Agency not found"));

            user.setAgence(agence);
            User updatedUser = userService.saveUser(user);

            return ResponseEntity.ok(Map.of(
                    "code", "000",
                    "label", "Agency assigned successfully",
                    "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "code", "500",
                    "label", "Error assigning agency: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/users/{userId}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);

            // Add Step1 data if exists
            step1Service.findByUser(user).ifPresent(step1 -> {
                responseData.put("step1", step1);
            });

            // Add Step2 data if exists
            Step2Entity step2 = step2Service.findByUser(user);
            if (step2 != null) {
                responseData.put("step2", step2);
            }

            // Add UserData if exists
            UserData userData = userDataService.findByUser(user);
            if (userData != null) {
                responseData.put("userData", userData);
            }

            // Add Agence data if user has an assigned agency
            if (user.getAgence() != null) {
                responseData.put("agence", user.getAgence());
            }

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Appointment management for admin
    @GetMapping("/appointments")
    public ResponseEntity<List<Rendezvous>> getAllAppointments() {
        List<Rendezvous> appointments = rendezvousService.getAllRendezvous();
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Map<String, Object>> createAppointment(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rendezvous rendezvous = new Rendezvous();
            rendezvous.setUser(user);
            rendezvous.setDateTime(java.time.LocalDateTime.parse(request.get("dateTime").toString()));
            if (request.get("meetUrl") != null) {
                rendezvous.setMeetUrl(request.get("meetUrl").toString());
            }

            Rendezvous savedRendezvous = rendezvousService.saveRendezvous(rendezvous);

            return ResponseEntity.ok(Map.of(
                    "code", "000",
                    "label", "Appointment created successfully",
                    "appointment", savedRendezvous
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "code", "500",
                    "label", "Error creating appointment: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<Rendezvous> updateAppointment(
            @PathVariable Long id,
            @RequestBody Rendezvous rendezvous) {

        Optional<Rendezvous> existingOpt = rendezvousService.findById(id);
        if (!existingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Rendezvous existing = existingOpt.get();
        existing.setDateTime(rendezvous.getDateTime());
        existing.setMeetUrl(rendezvous.getMeetUrl());

        Rendezvous updated = rendezvousService.saveRendezvous(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAppointment(@PathVariable Long id) {
        try {
            rendezvousService.deleteRendezvous(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}