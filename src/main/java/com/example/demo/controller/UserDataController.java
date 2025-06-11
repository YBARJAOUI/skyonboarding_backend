package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserData;
import com.example.demo.service.UserDataService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/userdata")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserService userService;

    @PostMapping("/data")
    public ResponseEntity<Map<String, String>> postUserData(
            @RequestBody UserData userData,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserData existing = userDataService.findByUser(user);
            UserData savedUserData;

            if (existing != null) {
                // Update all relevant fields
                existing.setAddress(userData.getAddress());
                existing.setFirstName(userData.getFirstName());
                existing.setLastName(userData.getLastName());
                existing.setCinId(userData.getCinId());
                existing.setBirthDate(userData.getBirthDate());
                existing.setBirthPlace(userData.getBirthPlace());
                existing.setSexe(userData.getSexe());
                existing.setSelfieFace(userData.getSelfieFace());
                existing.setDocfront(userData.getDocfront());
                existing.setDocback(userData.getDocback());

                savedUserData = userDataService.saveUserData(existing);

                if (savedUserData != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "User data updated successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to update user data"
                    ));
                }
            } else {
                // Create new user data
                userData.setUser(user);
                savedUserData = userDataService.saveUserData(userData);

                if (savedUserData != null && savedUserData.getId() != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "User data created successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to create user data"
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