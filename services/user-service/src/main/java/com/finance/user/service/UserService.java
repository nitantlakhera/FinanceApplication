package com.finance.user.service;

import com.finance.user.domain.User;
import com.finance.user.domain.UserRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @CachePut(value = "users", key = "#result.id")
    public User createUser(String email, String firstName, String lastName) {
        User user = new User(email, firstName, lastName);
        return userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }
}
