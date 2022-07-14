package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.entity.Todo;
import com.bulentyilmaz.todoapp.entity.User;
import com.bulentyilmaz.todoapp.exception.UnauthorizedException;
import com.bulentyilmaz.todoapp.exception.UserAlreadyExistsException;
import com.bulentyilmaz.todoapp.model.request.PasswordRequest;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.request.UpdateUserRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.bulentyilmaz.todoapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long getAuthenticatedUserId() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal.equals("anonymousUser")) {
            throw new UnauthorizedException("Unauthorized");
        }
        return Long.parseLong(principal);
    }

    public Optional<User> getAuthenticatedUser() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal.equals("anonymousUser")) {
            return Optional.empty();
        }
        return userRepository.findById(Long.parseLong(principal));
    }

    public List<UserResponse> getUsers(String firstName, String lastName, String email, Role role) {
        Integer intRole = null;
        if(role != null) intRole = role.ordinal();

        if(getAuthenticatedUser().get().getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only admins can view all users");
        }
        return convertToResponse(userRepository.findUsers(firstName, lastName, email, intRole));
    }

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
        return UserResponse.fromEntity(user);
    }

    public ResponseEntity<UserResponse> updateUser(Long userId, UpdateUserRequest body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
        if(getAuthenticatedUserId() != userId && getAuthenticatedUser().get().getRole()!= Role.ADMIN) {
            throw new UnauthorizedException("Only admins can update other users");
        }
        user.setFirstName(body.getFirstName());
        user.setLastName(body.getLastName());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    public ResponseEntity<UserResponse> changePassword(Long userId, PasswordRequest body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
        user.setPasswordHash(passwordEncoder.encode(body.getPassword()));
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    private List<UserResponse> convertToResponse(List<User> users) {
        List<UserResponse> userResponses = new ArrayList<>();
        for(User t: users) {
            userResponses.add(UserResponse.fromEntity(t));
        }
        return userResponses;
    }
}
