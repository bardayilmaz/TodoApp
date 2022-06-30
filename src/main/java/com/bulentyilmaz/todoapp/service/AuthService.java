package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.User;
import com.bulentyilmaz.todoapp.exception.PasswordMismatchException;
import com.bulentyilmaz.todoapp.exception.UserAlreadyExistsException;
import com.bulentyilmaz.todoapp.exception.UserNotFoundException;
import com.bulentyilmaz.todoapp.model.request.LoginRequest;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.response.AuthResponse;
import com.bulentyilmaz.todoapp.repository.UserRepository;
import com.bulentyilmaz.todoapp.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest registerRequest) {
        User exists = userRepository.findByEmail(registerRequest.getEmail());
        if(exists != null) {
            throw new UserAlreadyExistsException("Given user already exists!");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setTodos(new ArrayList<>());
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if(user == null) {
            throw new UserNotFoundException("User not found");
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new PasswordMismatchException("Wrong password");
        }

        return AuthResponse.builder()
                .id(user.getId())
                .token(jwtService.createToken(user.getId().toString()))
                .build();
    }
}
