package com.example.demo.controller;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import com.example.demo.service.RendezvousService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezvousController {

    @Autowired
    private RendezvousService rendezvousService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Rendezvous>> getAllRendezvous() {
        List<Rendezvous> rendezvousList = rendezvousService.getAllRendezvous();
        return ResponseEntity.ok(rendezvousList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rendezvous> getRendezvousById(@PathVariable Long id) {
        return rendezvousService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Rendezvous> createOrUpdateRendezvous(
            @RequestBody Rendezvous rendezvous,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rendezvous existing = rendezvousService.findByUser(user);
        Rendezvous savedRendezvous;
        if (existing != null) {
            existing.setDateTime(rendezvous.getDateTime());
            existing.setMeetUrl(rendezvous.getMeetUrl());
            savedRendezvous = rendezvousService.saveRendezvous(existing);
        } else {
            rendezvous.setUser(user);
            savedRendezvous = rendezvousService.saveRendezvous(rendezvous);
        }
        return ResponseEntity.ok(savedRendezvous);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Rendezvous> updateRendezvous(
            @PathVariable Long id,
            @RequestBody Rendezvous rendezvous) {

        Optional<Rendezvous> existingOpt = rendezvousService.findById(id);
        if (!existingOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Rendezvous existing = existingOpt.get();
        existing.setDateTime(rendezvous.getDateTime());
        existing.setMeetUrl(rendezvous.getMeetUrl());

        Rendezvous updated = rendezvousService.saveRendezvous(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteRendezvous(@PathVariable Long id) {
        try {
            rendezvousService.deleteRendezvous(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}