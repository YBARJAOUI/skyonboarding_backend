package com.example.demo.repository;

import com.example.demo.entity.Agence;
import com.example.demo.entity.Step1Entity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AgenceRepository extends JpaRepository<Agence, Long> {
}
