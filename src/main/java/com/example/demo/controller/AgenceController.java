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
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Agence agence = agenceService.findById(agenceId)
                    .orElseThrow(() -> new RuntimeException("Agence not found"));

            // Assign agence to user
            user.setAgence(agence);
            User updatedUser = userService.saveUser(user);

            if (updatedUser != null && updatedUser.getAgence() != null &&
                    updatedUser.getAgence().getId().equals(agenceId)) {
                // SUCCESS - Return 000
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Agence assigned successfully"
                ));
            } else {
                // FAILURE - Return 500
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "Failed to assign agence to user"
                ));
            }

        } catch (RuntimeException e) {
            // Handle specific business logic errors
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            // FAILURE - Return 500 for any other exceptions
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