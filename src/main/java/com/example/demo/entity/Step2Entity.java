package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "step2")
public class Step2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String familiareSituation;

    @Column(nullable = false)
    private String workStation;

    @Column(nullable = false)
    private String professionalActivity;

    @Column(nullable = false)
    private String secteur;

    @Column(nullable = false)
    private String revenueType;

    @Column(nullable = false)
    private String contratType;

    @Column(nullable = false)
    private double revenuMensuel;

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

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFamiliareSituation() {
        return familiareSituation;
    }

    public void setFamiliareSituation(String familiareSituation) {
        this.familiareSituation = familiareSituation;
    }

    public String getWorkStation() {
        return workStation;
    }

    public void setWorkStation(String workStation) {
        this.workStation = workStation;
    }

    public String getProfessionalActivity() {
        return professionalActivity;
    }

    public void setProfessionalActivity(String professionalActivity) {
        this.professionalActivity = professionalActivity;
    }

    public String getSecteur() {
        return secteur;
    }

    public void setSecteur(String secteur) {
        this.secteur = secteur;
    }

    public String getRevenueType() {
        return revenueType;
    }

    public void setRevenueType(String revenueType) {
        this.revenueType = revenueType;
    }

    public String getContratType() {
        return contratType;
    }

    public void setContratType(String contratType) {
        this.contratType = contratType;
    }

    public double getRevenuMensuel() {
        return revenuMensuel;
    }

    public void setRevenuMensuel(double revenuMensuel) {
        this.revenuMensuel = revenuMensuel;
    }


}