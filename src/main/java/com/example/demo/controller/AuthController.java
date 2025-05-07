package com.example.demo.controller;

import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    private Map<String, Object> loadLocaleJson(String localeLangage) {
        String fileName = "dropdowns_en.json";
        if ("fr".equalsIgnoreCase(localeLangage)) {
            fileName = "dropdowns_fr.json";
        }
        try (InputStream is = new ClassPathResource(fileName).getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, Map.class);
        } catch (IOException e) {
            // fallback to English if file not found or error
            try (InputStream is = new ClassPathResource("dropdowns_en.json").getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(is, Map.class);
            } catch (IOException ex) {
                return Map.of(); // empty map as last resort
            }
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        if (userService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of(
                "code", "001",
                "label", "Nom d'utilisateur déjà existant"
            ));
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNationality(request.getNationality());
        user.setUsernameEmail(request.isUsernameEmail());
        user.setSkyOrigin(request.getSkyOrigin());
        
        User savedUser = userService.registerUser(user);
        return ResponseEntity.ok(Map.of(
            "code", "000",
            "label", "succès"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String localeLangage = loginRequest.getOrDefault("localeLangage", "en");

        return userService.findByUsername(username)
                .filter(user -> userService.checkPassword(user, password))
                .map(user -> {
                    String token = jwtUtil.generateToken(username);
                    String refreshToken = jwtUtil.generateToken(username);

                    Map<String, Object> staticJson = loadLocaleJson(localeLangage);

                    Map<String, Object> response = new HashMap<>();
                    response.put("code", "000");
                    response.put("label", "succès");
                    response.put("token", token);
                    response.put("refreshToken", refreshToken);
                    response.put("staticJson", staticJson);

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).body(Map.of(
                    "code", "002",
                    "label", "Identifiants invalides"
                )));
    }

    @PutMapping("/update-signature-carte")
    public ResponseEntity<User> updateSignatureAndCarteType(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> updateRequest) {
        String signature = updateRequest.get("signature");
        String carteType = updateRequest.get("carteType");
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        User updatedUser = userService.updateSignatureAndCarteType(user.getId(), signature, carteType);
        return ResponseEntity.ok(updatedUser);
    }
}