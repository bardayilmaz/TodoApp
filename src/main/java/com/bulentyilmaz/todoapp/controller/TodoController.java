package com.bulentyilmaz.todoapp.controller;

import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getTodos(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate) {
        return todoService.getTodos(description, dueDate);
    }

    @GetMapping(path="/{id}")
    public TodoResponse getTodoById(@PathVariable("id") Long id) {
        return todoService.getTodoById(id);
    }

    @GetMapping("/of/{userId}")
    public List<TodoResponse> getTodosOf(@PathVariable("userId") Long userId) {
        return todoService.getTodosOf(userId);
    }

    @PostMapping()
    public TodoResponse addTodo(@Valid @RequestBody TodoRequest todoRequest) {
        return todoService.addNewTodo(todoRequest);
    }

    @DeleteMapping(path="{todoId}")
    public TodoResponse deleteTodo(@PathVariable("todoId") Long id) {
        return todoService.deleteTodo(id);
    }

    @PutMapping(path="/{id}")
    public void updateTodo(@PathVariable("id") Long id, @Valid @RequestBody TodoRequest todoRequest) {
        todoService.updateTodoById(id, todoRequest);
    }
}
