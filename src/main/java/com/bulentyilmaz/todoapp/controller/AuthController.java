package com.bulentyilmaz.todoapp.controller;

import com.bulentyilmaz.todoapp.model.request.LoginRequest;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.response.AuthResponse;
import com.bulentyilmaz.todoapp.service.AuthService;
import com.bulentyilmaz.todoapp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
