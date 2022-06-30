package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.User;
import com.bulentyilmaz.todoapp.exception.UnauthorizedException;
import com.bulentyilmaz.todoapp.exception.UserAlreadyExistsException;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.bulentyilmaz.todoapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized")); //???
        return UserResponse.fromEntity(user);
    }

    //public void updateUser(Long id, )



}
