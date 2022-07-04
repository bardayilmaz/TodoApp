package com.bulentyilmaz.todoapp.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class PasswordRequest {

    @NotEmpty(message = "password field can not be empty")
    @Min(8)
    private String password;
}
