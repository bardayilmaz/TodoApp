package com.bulentyilmaz.todoapp.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@ToString
public class RegisterRequest {

    @NotEmpty(message = "First name can not be empty!")
    private String firstName;

    @NotEmpty(message = "Last name can not be empty!")
    private String lastName;

    @Email(message = "Invalid email format!")
    @NotEmpty(message = "Email can not be empty!")
    private String email;

    @Size(min=8)
    @NotEmpty(message = "Password can not be empty!")
    private String password;
}
