package com.project.supportchatbot.service;

import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.exception.EmailAlreadyExistsException;
import com.project.supportchatbot.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user){

        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
