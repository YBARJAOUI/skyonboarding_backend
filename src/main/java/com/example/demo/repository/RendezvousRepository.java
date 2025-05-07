package com.example.demo.repository;

import com.example.demo.entity.Rendezvous;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezvousRepository extends JpaRepository<Rendezvous, Long> {
}