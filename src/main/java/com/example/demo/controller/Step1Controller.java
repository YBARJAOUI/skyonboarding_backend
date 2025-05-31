package com.example.demo.controller;

import com.example.demo.entity.Step1Entity;
import com.example.demo.entity.User;
import com.example.demo.service.Step1Service;
import com.example.demo.service.UserService;
import com.example.demo.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api/step1")
public class Step1Controller {

    @Autowired
    private Step1Service step1Service;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/data")
    public ResponseEntity<Map<String, String>> postStep1Data(
            @RequestBody Step1Entity step1Entity,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Step1Entity> existing = step1Service.findByUser(user);

            if (existing.isPresent()) {
                // Update the existing entity
                Step1Entity entityToUpdate = existing.get();
                entityToUpdate.setCountryQuestion(step1Entity.isCountryQuestion());
                entityToUpdate.setBirthCountry(step1Entity.getBirthCountry());
                entityToUpdate.setLivingQuestion(step1Entity.isLivingQuestion());
                entityToUpdate.setCreationReason(step1Entity.getCreationReason());

                // Save the updated entity
                step1Service.saveStep1Data(entityToUpdate);

                // SUCCESS - Return 000
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Data updated successfully"
                ));

            } else {
                // Create new entity
                step1Entity.setUser(user);
                Step1Entity savedEntity = step1Service.saveStep1Data(step1Entity);

                if (savedEntity != null && savedEntity.getId() != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Data created successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to save data"
                    ));
                }
            }

        } catch (Exception e) {
            // FAILURE - Return 500 for any exceptions
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
}