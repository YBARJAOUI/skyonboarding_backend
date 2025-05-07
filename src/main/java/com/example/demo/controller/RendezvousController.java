package com.example.demo.controller;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import com.example.demo.service.RendezvousService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rendezvous")
public class RendezvousController {

    @Autowired
    private RendezvousService rendezvousService;

    @Autowired
    private UserService userService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Rendezvous> createRendezvous(@PathVariable Long userId, @RequestBody Rendezvous rendezvous) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        rendezvous.setUser(user);
        Rendezvous savedRendezvous = rendezvousService.saveRendezvous(rendezvous);
        return ResponseEntity.ok(savedRendezvous);
    }
}