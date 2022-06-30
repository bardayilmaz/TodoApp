package com.bulentyilmaz.todoapp.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@ToString
public class LoginRequest {

    @Email(message = "invalid email!")
    @NotEmpty(message = "email can not be empty!")
    private String email;

    @Size(min=8)
    @NotEmpty(message = "password can not be empty")
    private String password;
}
