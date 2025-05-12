package com.example.demo.controller;

import com.example.demo.entity.Agence;
import com.example.demo.entity.User;
import com.example.demo.service.AgenceService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/assign/{userId}/{agenceId}")
    public ResponseEntity<User> assignAgenceToUser(@PathVariable Long userId, @PathVariable Long agenceId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Agence agence = agenceService.findById(agenceId).orElseThrow(() -> new RuntimeException("Agence not found"));
        user.setAgence(agence);
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
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