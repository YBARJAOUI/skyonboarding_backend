package com.example.demo.service;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import com.example.demo.repository.RendezvousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RendezvousService {

    @Autowired
    private RendezvousRepository rendezvousRepository;

    public Rendezvous saveRendezvous(Rendezvous rendezvous) {
        return rendezvousRepository.save(rendezvous);
    }

    public Rendezvous findByUser(User user) {
        return rendezvousRepository.findByUser(user).orElse(null);
    }
}