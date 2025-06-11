package com.example.demo.controller;

import com.example.demo.entity.Step1Entity;
import com.example.demo.entity.Step2Entity;
import com.example.demo.entity.User;
import com.example.demo.entity.UserData;
import com.example.demo.service.Step1Service;
import com.example.demo.service.Step2Service;
import com.example.demo.service.UserDataService;
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
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private Step1Service step1Service;
    
    @Autowired
    private Step2Service step2Service;
    
    @Autowired
    private UserDataService userDataService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails) {

        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Update user fields
                if (userDetails.getNationality() != null) {
                    user.setNationality(userDetails.getNationality());
                }
                if (userDetails.getSkyOrigin() != null) {
                    user.setSkyOrigin(userDetails.getSkyOrigin());
                }
                user.setUsernameEmail(userDetails.isUsernameEmail());

                User savedUser;

                // Update password if provided
                if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                    user.setPassword(userDetails.getPassword());
                    // Save with password encryption
                    savedUser = userService.updateUserWithPassword(user);
                } else {
                    // Save without password change
                    savedUser = userService.saveUser(user);
                }

                if (savedUser != null) {
                    // SUCCESS - Return 000
                    return ResponseEntity.ok(Map.of(
                            "status", "000",
                            "message", "User updated successfully"
                    ));
                } else {
                    // FAILURE - Return 500
                    return ResponseEntity.ok(Map.of(
                            "status", "500",
                            "message", "Failed to update user"
                    ));
                }
            } else {
                // User not found - Return 500
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "User not found"
                ));
            }

        } catch (Exception e) {
            // FAILURE - Return 500 for any exceptions
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-all-data")
    public ResponseEntity<?> getAllUserData(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create a response map to hold all the data
        Map<String, Object> responseData = new HashMap<>();
        
        // Add user data
        responseData.put("user", user);
        
        // Add Step1 data if exists
        step1Service.findByUser(user).ifPresent(step1 -> {
            responseData.put("step1", step1);
        });
        
        // Add Step2 data if exists
        Step2Entity step2 = step2Service.findByUser(user);
        if (step2 != null) {
            responseData.put("step2", step2);
        }
        
        // Add UserData if exists
        UserData userData = userDataService.findByUser(user);
        if (userData != null) {
            responseData.put("userData", userData);
        }
        
        // Add Agence data if user has an assigned agency
        if (user.getAgence() != null) {
            responseData.put("agence", user.getAgence());
        }
        
        return ResponseEntity.ok(responseData);
    }

    @PutMapping("/update-all-data")
    public ResponseEntity<Map<String, String>> updateAllUserData(
            @RequestBody Map<String, Object> allData,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            boolean allUpdatesSuccessful = true;
            StringBuilder errorMessages = new StringBuilder();

            // 1. Update User
            if (allData.containsKey("user")) {
                try {
                    Map<String, Object> userData = (Map<String, Object>) allData.get("user");
                    if (userData.containsKey("nationality")) user.setNationality((String) userData.get("nationality"));
                    if (userData.containsKey("skyOrigin")) user.setSkyOrigin((String) userData.get("skyOrigin"));
                    if (userData.containsKey("usernameEmail")) user.setUsernameEmail((Boolean) userData.get("usernameEmail"));

                    User savedUser = userService.saveUser(user);
                    if (savedUser == null) {
                        allUpdatesSuccessful = false;
                        errorMessages.append("Failed to update user data. ");
                    }
                } catch (Exception e) {
                    allUpdatesSuccessful = false;
                    errorMessages.append("User update failed: ").append(e.getMessage()).append(". ");
                }
            }

            // 2. Update UserData
            if (allData.containsKey("userData")) {
                try {
                    Map<String, Object> userDataMap = (Map<String, Object>) allData.get("userData");
                    UserData userData = Optional.ofNullable(userDataService.findByUser(user)).orElse(new UserData());

                    if (userDataMap.containsKey("firstName")) userData.setFirstName((String) userDataMap.get("firstName"));
                    if (userDataMap.containsKey("lastName")) userData.setLastName((String) userDataMap.get("lastName"));
                    if (userDataMap.containsKey("cinId")) userData.setCinId((String) userDataMap.get("cinId"));
                    if (userDataMap.containsKey("address")) userData.setAddress((String) userDataMap.get("address"));
                    if (userDataMap.containsKey("birthDate")) userData.setBirthDate((String) userDataMap.get("birthDate"));
                    if (userDataMap.containsKey("birthPlace")) userData.setBirthPlace((String) userDataMap.get("birthPlace"));
                    if (userDataMap.containsKey("sexe")) userData.setSexe((String) userDataMap.get("sexe"));
                    if (userDataMap.containsKey("selfieFace")) userData.setSelfieFace((String) userDataMap.get("selfieFace"));

                    userData.setUser(user);
                    UserData savedUserData = userDataService.saveUserData(userData);
                    if (savedUserData == null) {
                        allUpdatesSuccessful = false;
                        errorMessages.append("Failed to update user personal data. ");
                    }
                } catch (Exception e) {
                    allUpdatesSuccessful = false;
                    errorMessages.append("UserData update failed: ").append(e.getMessage()).append(". ");
                }
            }

            // 3. Update Step1
            if (allData.containsKey("step1")) {
                try {
                    Map<String, Object> step1Map = (Map<String, Object>) allData.get("step1");
                    Step1Entity step1Entity = step1Service.findByUser(user).orElse(new Step1Entity());

                    if (step1Map.containsKey("countryQuestion"))
                        step1Entity.setCountryQuestion((Boolean) step1Map.get("countryQuestion"));
                    if (step1Map.containsKey("birthCountry"))
                        step1Entity.setBirthCountry((String) step1Map.get("birthCountry"));
                    if (step1Map.containsKey("livingQuestion"))
                        step1Entity.setLivingQuestion((Boolean) step1Map.get("livingQuestion"));
                    if (step1Map.containsKey("creationReason"))
                        step1Entity.setCreationReason((String) step1Map.get("creationReason"));

                    step1Entity.setUser(user);
                    Step1Entity savedStep1 = step1Service.saveStep1Data(step1Entity);
                    if (savedStep1 == null) {
                        allUpdatesSuccessful = false;
                        errorMessages.append("Failed to update step1 data. ");
                    }
                } catch (Exception e) {
                    allUpdatesSuccessful = false;
                    errorMessages.append("Step1 update failed: ").append(e.getMessage()).append(". ");
                }
            }

            // 4. Update Step2
            if (allData.containsKey("step2")) {
                try {
                    Map<String, Object> step2Map = (Map<String, Object>) allData.get("step2");
                    Step2Entity step2Entity = step2Service.findByUser(user);

                    if (step2Entity == null) {
                        step2Entity = new Step2Entity();
                        step2Entity.setUser(user);
                    }

                    if (step2Map.containsKey("familiareSituation"))
                        step2Entity.setFamiliareSituation((String) step2Map.get("familiareSituation"));
                    if (step2Map.containsKey("workStation"))
                        step2Entity.setWorkStation((String) step2Map.get("workStation"));
                    if (step2Map.containsKey("professionalActivity"))
                        step2Entity.setProfessionalActivity((String) step2Map.get("professionalActivity"));
                    if (step2Map.containsKey("secteur"))
                        step2Entity.setSecteur((String) step2Map.get("secteur"));
                    if (step2Map.containsKey("revenueType"))
                        step2Entity.setRevenueType((String) step2Map.get("revenueType"));
                    if (step2Map.containsKey("contratType"))
                        step2Entity.setContratType((String) step2Map.get("contratType"));

                    if (step2Map.containsKey("revenuMensuel")) {
                        Object value = step2Map.get("revenuMensuel");
                        step2Entity.setRevenuMensuel(value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString()));
                    }

                    Step2Entity savedStep2 = step2Service.saveStep2Data(step2Entity);
                    if (savedStep2 == null) {
                        allUpdatesSuccessful = false;
                        errorMessages.append("Failed to update step2 data. ");
                    }
                } catch (Exception e) {
                    allUpdatesSuccessful = false;
                    errorMessages.append("Step2 update failed: ").append(e.getMessage()).append(". ");
                }
            }

            if (allUpdatesSuccessful) {
                // SUCCESS - Return 000
                return ResponseEntity.ok(Map.of(
                        "status", "000",
                        "message", "All data updated successfully"
                ));
            } else {
                // PARTIAL/COMPLETE FAILURE - Return 500
                return ResponseEntity.ok(Map.of(
                        "status", "500",
                        "message", "Update failed: " + errorMessages.toString().trim()
                ));
            }

        } catch (Exception e) {
            // FAILURE - Return 500 for any exceptions
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
    // Add this method to the existing UserController class

    @GetMapping("/{id}/all-data")
    public ResponseEntity<?> getUserDataById(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "status", "404",
                        "message", "User not found"
                ));
            }

            User user = userOptional.get();

            // Create a response map to hold all the data
            Map<String, Object> responseData = new HashMap<>();

            // Add user data
            responseData.put("user", user);

            // Add Step1 data if exists
            step1Service.findByUser(user).ifPresent(step1 -> {
                responseData.put("step1", step1);
            });

            // Add Step2 data if exists
            Step2Entity step2 = step2Service.findByUser(user);
            if (step2 != null) {
                responseData.put("step2", step2);
            }

            // Add UserData if exists
            UserData userData = userDataService.findByUser(user);
            if (userData != null) {
                responseData.put("userData", userData);
            }

            // Add Agence data if user has an assigned agency
            if (user.getAgence() != null) {
                responseData.put("agence", user.getAgence());
            }

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "status", "500",
                    "message", "Error: " + e.getMessage()
            ));
        }
    }
}