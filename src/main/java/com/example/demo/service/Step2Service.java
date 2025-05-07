package com.example.demo.service;

import com.example.demo.entity.Step2Entity;
import com.example.demo.entity.User;
import com.example.demo.repository.Step2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Step2Service {

    @Autowired
    private Step2Repository step2Repository;

    public Step2Entity saveStep2Data(Step2Entity step2Entity) {
        return step2Repository.save(step2Entity);
    }
    public Step2Entity findByUser(User user) {
    return step2Repository.findByUser(user).orElse(null);
}
}