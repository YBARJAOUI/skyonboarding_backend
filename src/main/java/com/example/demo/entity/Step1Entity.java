package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "step1")
public class Step1Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean countryQuestion;

    @Column(nullable = false)
    private String birthCountry;

    @Column(nullable = false)
    private boolean livingQuestion;

    @Column(nullable = false)
    private String creationReason;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCountryQuestion() {
        return countryQuestion;
    }

    public void setCountryQuestion(boolean countryQuestion) {
        this.countryQuestion = countryQuestion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public boolean isLivingQuestion() {
        return livingQuestion;
    }

    public void setLivingQuestion(boolean livingQuestion) {
        this.livingQuestion = livingQuestion;
    }

    public String getCreationReason() {
        return creationReason;
    }

    public void setCreationReason(String creationReason) {
        this.creationReason = creationReason;
    }
}