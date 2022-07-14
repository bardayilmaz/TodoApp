package com.bulentyilmaz.todoapp.model.response;

import com.bulentyilmaz.todoapp.entity.Todo;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
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
