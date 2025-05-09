package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_data")
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String cinId;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String birthPlace;

    @Column(nullable = false)
    private String sexe;


    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String selfieFace;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCinId() { return cinId; }
    public void setCinId(String cinId) { this.cinId = cinId; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getSelfieFace() { return selfieFace; }
    public void setSelfieFace(String selfieFace) { this.selfieFace = selfieFace; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}