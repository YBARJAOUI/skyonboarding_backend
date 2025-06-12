package com.example.demo.controller;

import com.example.demo.entity.Agence;
import com.example.demo.entity.User;
import com.example.demo.service.AgenceService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//here
@RestController
@RequestMapping("/api/agence")
public class AgenceController {

    @Autowired
    private AgenceService agenceService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Agence>> getAllAgences() {
        List<Agence> agences = agenceService.getAllAgences();
        return ResponseEntity.ok(agences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agence> getAgenceById(@PathVariable Long id) {
        return agenceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Agence> createAgence(@RequestBody Agence agence) {
        Agence savedAgence = agenceService.saveAgence(agence);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAgence);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agence> updateAgence(@PathVariable Long id, @RequestBody Agence agence) {
        try {
            Agence updatedAgence = agenceService.updateAgence(id, agence);
            return ResponseEntity.ok(updatedAgence);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteAgence(@PathVariable Long id) {
        try {
            agenceService.deleteAgence(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/assign/{agenceId}")
    public ResponseEntity<Map<String, String>> assignAgenceToCurrentUser(
            @PathVariable Long agenceId,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        try {
            System.out.println("=== AGENCE ASSIGNMENT DEBUG ===");
            System.out.println("User details: " + userDetails.getUsername());
            System.out.println("User authorities: " + userDetails.getAuthorities());
            System.out.println("Agence ID: " + agenceId);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("Found user: " + user.getUsername());
            System.out.println("User role: " + user.getRole());
            System.out.println("Current user agence: " + (user.getAgence() != null ? user.getAgence().getName() : "null"));

            Agence agence = agenceService.findById(agenceId)
                    .orElseThrow(() -> new RuntimeException("Agence not found"));

            System.out.println("Found agence: " + agence.getName());

            // Assign agence to user
            user.setAgence(agence);

            // Use the service method that properly handles the relationship
            User updatedUser = userService.saveUser(user);

            // Force refresh from database to verify the save
            User verifiedUser = userService.findById(updatedUser.getId())
                    .orElseThrow(() -> new RuntimeException("User verification failed"));

            System.out.println("After save - User agence: " +
                    (verifiedUser.getAgence() != null ? verifiedUser.getAgence().getName() : "null"));

            if (verifiedUser.getAgence() != null &&
                    verifiedUser.getAgence().getId().equals(agenceId)) {
                // SUCCESS - Return 000
                System.out.println("Assignment successful - verified in database");
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Agence assigned successfully"
                ));
            } else {
                // FAILURE - Return 500
                System.out.println("Assignment failed - verification failed");
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "Failed to assign agence to user - database verification failed"
                ));
            }

        } catch (RuntimeException e) {
            System.out.println("Runtime exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.out.println("General exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Agence>> getAgencesByCountry(@PathVariable String country) {
        List<Agence> agences = agenceService.findByCountry(country);
        return ResponseEntity.ok(agences);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Agence>> searchAgences(@RequestParam String name) {
        List<Agence> agences = agenceService.searchByName(name);
        return ResponseEntity.ok(agences);
    }
}