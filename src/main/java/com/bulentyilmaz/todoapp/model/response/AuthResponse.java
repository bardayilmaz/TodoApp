package com.bulentyilmaz.todoapp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
@Builder
public class AuthResponse {

    private Long id;
    private String token;
}
