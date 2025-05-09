package com.example.demo.repository;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RendezvousRepository extends JpaRepository<Rendezvous, Long> {
    Optional<Rendezvous> findByUser(User user);
}