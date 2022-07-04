package com.bulentyilmaz.todoapp.controller;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.model.request.PasswordRequest;
import com.bulentyilmaz.todoapp.model.request.UpdateUserRequest;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.bulentyilmaz.todoapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers(@RequestParam(required = false) String firstName,
                                       @RequestParam(required = false) String lastName,
                                       @RequestParam(required = false) String email,
                                       @RequestParam(required = false) Role role) {
        return userService.getUsers(firstName, lastName, email, role);
    }

    @GetMapping("/me")
    public UserResponse getUser() {
        return userService.getUser(userService.getAuthenticatedUserId());
    }

    @PutMapping("/me")
    public void updateUser(@Valid @RequestBody UpdateUserRequest body) {
        userService.updateUser(userService.getAuthenticatedUserId(), body);
    }

    @PutMapping("/me/pass")
    public void changePassword(@Valid @RequestBody PasswordRequest body) {
        userService.changePassword(userService.getAuthenticatedUserId(), body);
    }
}