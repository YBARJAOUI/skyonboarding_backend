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

@RestController
@RequestMapping("/api/userdata")
public class UserDataController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserService userService;

    @PostMapping("/data")
    public ResponseEntity<UserData> postUserData(@RequestBody UserData userData, @AuthenticationPrincipal UserDetails userDetails) {
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
            savedUserData = userDataService.saveUserData(existing);
        } else {
            userData.setUser(user);
            savedUserData = userDataService.saveUserData(userData);
        }
        return ResponseEntity.ok(savedUserData);
    }
}