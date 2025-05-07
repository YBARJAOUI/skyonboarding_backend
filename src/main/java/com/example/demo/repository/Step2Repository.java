package com.example.demo.repository;

import com.example.demo.entity.Step2Entity;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Step2Repository extends JpaRepository<Step2Entity, Long> {
Optional<Step2Entity> findByUser(User user);
}