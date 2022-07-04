package com.bulentyilmaz.todoapp.model.response;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    public static UserResponse fromEntity(User user) {
        if(user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
