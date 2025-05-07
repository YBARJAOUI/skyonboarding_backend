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
    public ResponseEntity<Step1Entity> postStep1Data(@RequestBody Step1Entity step1Entity, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    // Find if Step1Entity already exists for this user
    Optional<Step1Entity> existing = step1Service.findByUser(user);

    if (existing.isPresent()) {
        // Update the existing entity
        Step1Entity entityToUpdate = existing.get();
        entityToUpdate.setCountryQuestion(step1Entity.isCountryQuestion());
        entityToUpdate.setBirthCountry(step1Entity.getBirthCountry());
        entityToUpdate.setLivingQuestion(step1Entity.isLivingQuestion());
        entityToUpdate.setCreationReason(step1Entity.getCreationReason());
        Step1Entity savedEntity = step1Service.saveStep1Data(entityToUpdate);
        return ResponseEntity.ok(savedEntity);
    } else {
        // Create new entity
        step1Entity.setUser(user);
        Step1Entity savedEntity = step1Service.saveStep1Data(step1Entity);
        return ResponseEntity.ok(savedEntity);
    }
}


}