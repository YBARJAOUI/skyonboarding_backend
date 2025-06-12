package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nationality;

    @Column(nullable = false)
    private boolean usernameEmail;

    @Column(nullable = false)
    private String skyOrigin; // MOBILE ou WEB

    @Column(nullable = false)
    private int scanAttmpt = 0;

    @Column(nullable = false)
    private int faceAttampt = 0;

    @Column(columnDefinition = "LONGTEXT")
    private String signature;

    private String carteType;

    private boolean hasAccount = false;

    // Add role field
    @Column(nullable = false)
    private String role = "USER"; // USER, ADMIN

    // FIXED: Remove the agenceId field and make the relationship properly mutable
    // The issue was having both agenceId and the @ManyToOne relationship
    // This causes Hibernate to consider the relationship immutable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = true)
    private Agence agence;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Rendezvous> rendezvousList;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public boolean isUsernameEmail() { return usernameEmail; }
    public void setUsernameEmail(boolean usernameEmail) { this.usernameEmail = usernameEmail; }

    public String getSkyOrigin() { return skyOrigin; }
    public void setSkyOrigin(String skyOrigin) { this.skyOrigin = skyOrigin; }

    public int getScanAttmpt() { return scanAttmpt; }
    public void setScanAttmpt(int scanAttmpt) { this.scanAttmpt = scanAttmpt; }

    public int getFaceAttampt() { return faceAttampt; }
    public void setFaceAttampt(int faceAttampt) { this.faceAttampt = faceAttampt; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getCarteType() { return carteType; }
    public void setCarteType(String carteType) { this.carteType = carteType; }

    public boolean getHasAccount() { return hasAccount; }
    public void setHasAccount(boolean hasAccount) { this.hasAccount = hasAccount; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Fixed agence getter and setter
    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    // Helper method to get agence ID (for JSON serialization if needed)
    public Long getAgenceId() {
        return agence != null ? agence.getId() : null;
    }

    public List<Rendezvous> getRendezvousList() {
        return rendezvousList;
    }

    public void setRendezvousList(List<Rendezvous> rendezvousList) {
        this.rendezvousList = rendezvousList;
    }
}