package com.pm.authservice.service;

import com.pm.authservice.model.User;
import com.pm.authservice.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }
  
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public User save(User user) {
    return userRepository.save(user);
  }
}
