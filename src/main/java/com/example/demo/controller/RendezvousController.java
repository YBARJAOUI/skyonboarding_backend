package com.example.demo.controller;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import com.example.demo.service.RendezvousService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezvousController {

    @Autowired
    private RendezvousService rendezvousService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Rendezvous> createOrUpdateRendezvous(
            @RequestBody Rendezvous rendezvous,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        Rendezvous existing = rendezvousService.findByUser(user);
        Rendezvous savedRendezvous;
        if (existing != null) {
            // Update all relevant fields
            existing.setDateTime(rendezvous.getDateTime());
            existing.setMeetUrl(rendezvous.getMeetUrl()); // <-- add this line
            // ... update other fields if you add more ...
            savedRendezvous = rendezvousService.saveRendezvous(existing);
        } else {
            rendezvous.setUser(user);
            savedRendezvous = rendezvousService.saveRendezvous(rendezvous);
        }
        return ResponseEntity.ok(savedRendezvous);
    }
}