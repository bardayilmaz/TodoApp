package com.bulentyilmaz.todoapp.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Data
public class TodoRequest {

    @NotEmpty(message = "description can not be empty")
    private String description;

    private LocalDate dueDate;

}
