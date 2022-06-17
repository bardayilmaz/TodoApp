package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.Todo;
import com.bulentyilmaz.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    public void addNewTodo(Todo todo) {
        Optional<Todo> todoByEmail = todoRepository.findTodoByDescription(todo.getDescription());
        if(todoByEmail.isPresent()) {
            throw new IllegalStateException("todo already added");
        }
        todoRepository.save(todo);
    }
}
