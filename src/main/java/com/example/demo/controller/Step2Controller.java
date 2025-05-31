package com.example.demo.controller;

import com.example.demo.entity.Step2Entity;
import com.example.demo.entity.User;
import com.example.demo.service.Step2Service;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/step2")
public class Step2Controller {

    @Autowired
    private Step2Service step2Service;

    @Autowired
    private UserService userService;

    @PostMapping("/data")
    public ResponseEntity<Map<String, String>> postStep2Data(
            @RequestBody Step2Entity step2Entity,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Step2Entity existing = step2Service.findByUser(user);
            Step2Entity savedStep2Entity;

            if (existing != null) {
                // Update all relevant fields
                existing.setFamiliareSituation(step2Entity.getFamiliareSituation());
                existing.setWorkStation(step2Entity.getWorkStation());
                existing.setProfessionalActivity(step2Entity.getProfessionalActivity());
                existing.setSecteur(step2Entity.getSecteur());
                existing.setRevenueType(step2Entity.getRevenueType());
                existing.setContratType(step2Entity.getContratType());
                existing.setRevenuMensuel(step2Entity.getRevenuMensuel());

                savedStep2Entity = step2Service.saveStep2Data(existing);

                if (savedStep2Entity != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Personal information updated successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to update personal information"
                    ));
                }
            } else {
                // Create new step2 entity
                step2Entity.setUser(user);
                savedStep2Entity = step2Service.saveStep2Data(step2Entity);

                if (savedStep2Entity != null && savedStep2Entity.getId() != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Personal information created successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to create personal information"
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