package com.example.demo.service;

import com.example.demo.entity.Agence;
import com.example.demo.repository.AgenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgenceService {

    @Autowired
    private AgenceRepository agenceRepository;

    public List<Agence> getAllAgences() {
        return agenceRepository.findAll();
    }

    public Optional<Agence> findById(Long id) {
        return agenceRepository.findById(id);
    }

    public Agence saveAgence(Agence agence) {
        return agenceRepository.save(agence);
    }

    public void deleteAgence(Long id) {
        agenceRepository.deleteById(id);
    }

    public Agence updateAgence(Long id, Agence agenceDetails) {
        Agence agence = agenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agency not found with id: " + id));

        agence.setName(agenceDetails.getName());
        agence.setAddress(agenceDetails.getAddress());
        agence.setCountry(agenceDetails.getCountry());
        agence.setLatitude(agenceDetails.getLatitude());
        agence.setLongitude(agenceDetails.getLongitude());

        return agenceRepository.save(agence);
    }

    public List<Agence> findByCountry(String country) {
        return agenceRepository.findByCountry(country);
    }

    public Optional<Agence> findByName(String name) {
        return agenceRepository.findByName(name);
    }

    public List<Agence> searchByName(String name) {
        return agenceRepository.findByNameContainingIgnoreCase(name);
    }
}