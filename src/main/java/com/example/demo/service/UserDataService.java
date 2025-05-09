package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserData;
import com.example.demo.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {

    @Autowired
    private UserDataRepository userDataRepository;

    public UserData saveUserData(UserData userData) {
        return userDataRepository.save(userData);
    }

    public UserData findByUser(User user) {
        return userDataRepository.findByUser(user).orElse(null);
    }
}