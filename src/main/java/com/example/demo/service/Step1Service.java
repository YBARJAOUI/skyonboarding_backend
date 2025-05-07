package com.example.demo.service;

import com.example.demo.entity.Step1Entity;
import com.example.demo.entity.User;
import com.example.demo.repository.Step1Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Step1Service {
    @Autowired
    private Step1Repository step1Repository;

    public Step1Entity saveStep1Data(Step1Entity step1Entity) {
        return step1Repository.save(step1Entity);
    }

    public Optional<Step1Entity> findByUser(User user) {
        return step1Repository.findByUser(user);
    }
}