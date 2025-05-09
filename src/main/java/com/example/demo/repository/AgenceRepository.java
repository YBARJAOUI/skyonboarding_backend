package com.example.demo.repository;

import com.example.demo.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgenceRepository extends JpaRepository<Agence, Long> {
    List<Agence> findByCountry(String country);
    Optional<Agence> findByName(String name);
    List<Agence> findByNameContainingIgnoreCase(String name);
}