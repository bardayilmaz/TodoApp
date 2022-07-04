package com.bulentyilmaz.todoapp.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class UpdateUserRequest {

    @NotEmpty(message = "First name field can not be empty")
    private String firstName;

    @NotEmpty(message = "First name field can not be empty")
    private String lastName;
}
