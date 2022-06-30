package com.bulentyilmaz.todoapp.controller;

import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.bulentyilmaz.todoapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getUser() {
        return userService.getUser((userService.getAuthenticatedUserId()));
    }

    //public void

}
