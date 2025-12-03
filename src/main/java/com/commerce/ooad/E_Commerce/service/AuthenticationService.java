package com.commerce.ooad.E_Commerce.service;

import com.commerce.ooad.E_Commerce.model.UserSQL;
import com.commerce.ooad.E_Commerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserSQL> authenticate(String username, String password) {
        Optional<UserSQL> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            UserSQL user = userOptional.get();
            if (user.authenticate(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public UserSQL register(UserSQL user) throws RegistrationException {
        validateRegistration(user);
        return userRepository.save(user);
    }

    private void validateRegistration(UserSQL user) throws RegistrationException {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RegistrationException("Username cannot be empty.");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RegistrationException("Password cannot be empty.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RegistrationException("Email cannot be empty.");
        }
        if (user.getState() == null || user.getState().isEmpty()) {
            throw new RegistrationException("Please select your state.");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RegistrationException("Username already exists. Please choose another.");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("Email already exists. Please choose another.");
        }
    }

    public static class RegistrationException extends Exception {
        public RegistrationException(String message) {
            super(message);
        }
    }
}