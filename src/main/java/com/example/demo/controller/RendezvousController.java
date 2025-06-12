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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentRendezvous(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rendezvous rendezvous = rendezvousService.findByUser(user);

            if (rendezvous != null) {
                Map<String, Object> rendezvousData = new HashMap<>();
                rendezvousData.put("id", rendezvous.getId());
                rendezvousData.put("dateTime", rendezvous.getDateTime().toString());
                rendezvousData.put("meetUrl", rendezvous.getMeetUrl());
                rendezvousData.put("status", rendezvous.getStatus().toString());
                rendezvousData.put("adminNotes", rendezvous.getAdminNotes());
                rendezvousData.put("createdAt", rendezvous.getCreatedAt().toString());
                rendezvousData.put("updatedAt", rendezvous.getUpdatedAt() != null ?
                        rendezvous.getUpdatedAt().toString() : null);

                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Rendez-vous trouvé",
                        "rendezvous", rendezvousData
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "404",
                        "message", "Aucun rendez-vous trouvé"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Erreur: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createOrUpdateRendezvous(
            @RequestBody Rendezvous rendezvous,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rendezvous existing = rendezvousService.findByUser(user);
            Rendezvous savedRendezvous;

            if (existing != null) {
                existing.setDateTime(rendezvous.getDateTime());
                existing.setMeetUrl(rendezvous.getMeetUrl());
                existing.setStatus(Rendezvous.RendezvousStatus.PENDING); // Reset to pending when updated
                savedRendezvous = rendezvousService.saveRendezvous(existing);

                if (savedRendezvous != null) {
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Rendezvous updated successfully"
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to update rendezvous"
                    ));
                }
            } else {
                rendezvous.setUser(user);
                rendezvous.setStatus(Rendezvous.RendezvousStatus.PENDING);
                savedRendezvous = rendezvousService.saveRendezvous(rendezvous);

                if (savedRendezvous != null && savedRendezvous.getId() != null) {
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Rendezvous created successfully"
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to create rendezvous"
                    ));
                }
            }

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateRendezvous(
            @RequestBody Map<String, Object> updateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rendezvous existing = rendezvousService.findByUser(user);
            if (existing == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "404",
                        "message", "No rendezvous found to update"
                ));
            }

            // Update dateTime if provided
            if (updateRequest.containsKey("dateTime")) {
                String dateTimeStr = (String) updateRequest.get("dateTime");
                existing.setDateTime(LocalDateTime.parse(dateTimeStr));
            }

            // Update meetUrl if provided
            if (updateRequest.containsKey("meetUrl")) {
                existing.setMeetUrl((String) updateRequest.get("meetUrl"));
            }

            // Reset status to pending when user updates
            existing.setStatus(Rendezvous.RendezvousStatus.PENDING);

            Rendezvous savedRendezvous = rendezvousService.saveRendezvous(existing);

            if (savedRendezvous != null) {
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Rendezvous updated successfully"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "Failed to update rendezvous"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelRendezvous(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rendezvous existing = rendezvousService.findByUser(user);
            if (existing == null) {
                return ResponseEntity.ok(Map.of(
                        "status", "404",
                        "message", "No rendezvous found to cancel"
                ));
            }

            existing.setStatus(Rendezvous.RendezvousStatus.CANCELLED);
            Rendezvous savedRendezvous = rendezvousService.saveRendezvous(existing);

            if (savedRendezvous != null) {
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "Rendezvous cancelled successfully"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "Failed to cancel rendezvous"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
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

    // Admin endpoints for managing rendezvous status
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<Map<String, String>> updateRendezvousStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusRequest) {

        try {
            Optional<Rendezvous> existingOpt = rendezvousService.findById(id);
            if (!existingOpt.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "status", "404",
                        "message", "Rendezvous not found"
                ));
            }

            Rendezvous existing = existingOpt.get();
            String statusStr = (String) statusRequest.get("status");
            String adminNotes = (String) statusRequest.get("adminNotes");

            try {
                Rendezvous.RendezvousStatus newStatus = Rendezvous.RendezvousStatus.valueOf(statusStr.toUpperCase());
                existing.setStatus(newStatus);
                if (adminNotes != null) {
                    existing.setAdminNotes(adminNotes);
                }

                Rendezvous savedRendezvous = rendezvousService.saveRendezvous(existing);

                if (savedRendezvous != null) {
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "Rendezvous status updated successfully"
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to update rendezvous status"
                    ));
                }

            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(Map.of(
                        "status", "400",
                        "message", "Invalid status value"
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
}