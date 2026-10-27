package com.project.supportchatbot.controller;

import com.project.supportchatbot.dto.LoginRequest;
import com.project.supportchatbot.dto.LoginResponse;
import com.project.supportchatbot.dto.RegisterRequest;
import com.project.supportchatbot.dto.UserResponse;
import com.project.supportchatbot.entity.User;
import com.project.supportchatbot.service.JWTService;
import com.project.supportchatbot.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;

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

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        User user = userService.login(request);

        String token = jwtService.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );

        return new LoginResponse(
                token,
                "Bearer",
                userResponse
        );
    }
}
