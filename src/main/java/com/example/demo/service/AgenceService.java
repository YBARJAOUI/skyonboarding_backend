package com.example.demo.service;

import com.example.demo.entity.Agence;
import com.example.demo.repository.AgenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgenceService {

    @Autowired
    private AgenceRepository agenceRepository;

    public Agence saveAgence(Agence agence) {
        return agenceRepository.save(agence);
    }

    public Optional<Agence> findById(Long id) {
        return agenceRepository.findById(id);
    }
}