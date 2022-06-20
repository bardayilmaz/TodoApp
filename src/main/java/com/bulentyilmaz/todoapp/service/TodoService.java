package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.Todo;
import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> getTodos(String description, LocalDate dueDate) {
        if(description != null && dueDate==null) {
            if(dueDate == null) {
                return convertToResponse(todoRepository.findTodosByDescription(description));
            }
            return convertToResponse(todoRepository.findTodosByDescriptionAndDueDate(description, dueDate));
        }
        else{
            if(dueDate == null) {
                return convertToResponse(todoRepository.findAll());
            }
            return convertToResponse(todoRepository.findTodosByDueDate(dueDate));
        }
    }

    public TodoResponse getTodoById(Long id) {
        Optional<Todo> todo = todoRepository.findById(id);
        if(todo == null) {
            throw new IllegalStateException("Todo with id " + id + " does not exist.");
        }
        return TodoResponse.fromEntity(todo.get());
    }


    public TodoResponse addNewTodo(TodoRequest todoRequest) {
        if(todoRequest.getDueDate()!=null && !isGivenDueDateValid(todoRequest.getDueDate())) {
            throw new IllegalStateException("Given dueDate is not valid.");
        }
        Todo newTodo = fromRequest(new Todo(), todoRequest);
        return TodoResponse.fromEntity(todoRepository.save(newTodo));
    }

    public TodoResponse deleteTodo(Long id) {
        boolean exists = todoRepository.existsById(id);
        if(!exists) {
            throw new IllegalStateException("todo with id " + id + " is not exists.");
        }
        Optional<Todo> removed = todoRepository.findById(id);
        todoRepository.deleteById(id);
        return TodoResponse.fromEntity(removed.get());
    }

    public ResponseEntity<Todo> updateTodoById(Long id, TodoRequest todoRequest) {
        String description = todoRequest.getDescription();
        LocalDate dueDate = todoRequest.getDueDate();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("todo with id " + id +" does not exists"));

        if(dueDate!=null && !isGivenDueDateValid(dueDate)) {
            throw new IllegalStateException("Given dueDate is not valid.");
        }
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        final Todo updatedTodo = todoRepository.save(todo);
        return ResponseEntity.ok(updatedTodo);
    }

    private Todo fromRequest(Todo todo, TodoRequest todoRequest) {
        todo.setDescription(todoRequest.getDescription());
        todo.setDueDate(todoRequest.getDueDate());
        return todo;
    }

    private List<TodoResponse> convertToResponse(List<Todo> todos) {
        List<TodoResponse> todoResponses = new ArrayList<>();
        for(Todo t: todos) {
            todoResponses.add(TodoResponse.fromEntity(t));
        }
        return todoResponses;
    }

    private boolean isGivenDueDateValid(LocalDate dueDate) {
        return LocalDate.now().compareTo(dueDate) <= 0;
    }

}
