package com.project.supportchatbot.controller;

import com.project.supportchatbot.dto.RegisterRequest;
import com.project.supportchatbot.dto.UserResponse;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponse  registerUser(@Valid @RequestBody RegisterRequest registerRequest){

        User user = new User();

        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        User savedUser = userService.registerUser(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );

    }
}
