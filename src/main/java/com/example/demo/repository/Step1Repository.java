package com.example.demo.repository;

import com.example.demo.entity.Step1Entity;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Step1Repository extends JpaRepository<Step1Entity, Long> {

    Optional<Step1Entity> findByUser(User user);
}