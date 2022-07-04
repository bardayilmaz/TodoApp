package com.bulentyilmaz.todoapp.model.response;

import com.bulentyilmaz.todoapp.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class TodoResponse {

    private Long id;
    private String description;
    private LocalDate dueDate;
    private Long userId;

    public static TodoResponse fromEntity(Todo todo) {
        return new TodoResponseBuilder()
                .id(todo.getId())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .userId(todo.getOwner().getId())
                .build();
    }
}
