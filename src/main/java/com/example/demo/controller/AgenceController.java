package com.example.demo.controller;

import com.example.demo.entity.Agence;
import com.example.demo.entity.User;
import com.example.demo.service.AgenceService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agence")
public class AgenceController {

    @Autowired
    private AgenceService agenceService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Agence> createAgence(@RequestBody Agence agence) {
        Agence savedAgence = agenceService.saveAgence(agence);
        return ResponseEntity.ok(savedAgence);
    }

    @PutMapping("/assign/{userId}/{agenceId}")
    public ResponseEntity<User> assignAgenceToUser(@PathVariable Long userId, @PathVariable Long agenceId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Agence agence = agenceService.findById(agenceId).orElseThrow(() -> new RuntimeException("Agence not found"));
        user.setAgence(agence);
        User updatedUser = userService.saveUser(user);
        return ResponseEntity.ok(updatedUser);
    }
}