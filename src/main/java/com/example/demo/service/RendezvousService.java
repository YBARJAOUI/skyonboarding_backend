package com.example.demo.service;

import com.example.demo.entity.Rendezvous;
import com.example.demo.entity.User;
import com.example.demo.repository.RendezvousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RendezvousService {

    @Autowired
    private RendezvousRepository rendezvousRepository;

    public List<Rendezvous> getAllRendezvous() {
        return rendezvousRepository.findAll();
    }

    public Optional<Rendezvous> findById(Long id) {
        return rendezvousRepository.findById(id);
    }

    public Rendezvous saveRendezvous(Rendezvous rendezvous) {
        return rendezvousRepository.save(rendezvous);
    }

    public Rendezvous findByUser(User user) {
        return rendezvousRepository.findByUser(user).orElse(null);
    }

    public void deleteRendezvous(Long id) {
        rendezvousRepository.deleteById(id);
    }
}