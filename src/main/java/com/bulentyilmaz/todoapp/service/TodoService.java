package com.bulentyilmaz.todoapp.service;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.entity.Todo;
import com.bulentyilmaz.todoapp.exception.InvalidDueDateException;
import com.bulentyilmaz.todoapp.exception.TodoDoesNotExistException;
import com.bulentyilmaz.todoapp.exception.UnauthorizedException;
import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    @Autowired
    public TodoService(TodoRepository todoRepository, UserService userService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    public List<TodoResponse> getTodos(String description, LocalDate dueDate) {
        if(userService.getAuthenticatedUser().get().getRole() == Role.ADMIN) {
            return convertToResponse(todoRepository.findTodos(description, dueDate));
        }
        return convertToResponse(todoRepository.findTodosByOwnerId(userService.getAuthenticatedUserId()));
    }

    public TodoResponse getTodoById(Long todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if(todo.isPresent() &&
                !convertToResponse(todoRepository.findTodosByOwnerId(userService.getAuthenticatedUserId())).contains(todo.get())) {
            throw new UnauthorizedException("You are unauthorized to view someone else's todo!");
        }
        if(todo.isEmpty()) {
            throw new TodoDoesNotExistException("Todo with id " + todoId + " does not exist.");
        }
        return TodoResponse.fromEntity(todo.get());
    }


    public TodoResponse addNewTodo(TodoRequest todoRequest) {
        if(todoRequest.getDueDate()!=null && !isGivenDueDateValid(todoRequest.getDueDate())) {
            throw new InvalidDueDateException("Given dueDate is not valid.");
        }
        Todo newTodo = fromRequest(new Todo(), todoRequest);
        newTodo.setOwner(userService.getAuthenticatedUser().get());
        return TodoResponse.fromEntity(todoRepository.save(newTodo));
    }

    public TodoResponse deleteTodo(Long id) {
        boolean exists = todoRepository.existsById(id);
        if(!exists) {
            throw new TodoDoesNotExistException("Todo with id " + id + " does not exist.");
        }
        Optional<Todo> removed = todoRepository.findById(id);
        long userId = userService.getAuthenticatedUserId();

        if(removed.get().getOwner().getId() != userId) {
            throw new UnauthorizedException("You can not delete someone else's todo!");
        }
        todoRepository.deleteById(id);
        return TodoResponse.fromEntity(removed.get());
    }

    public ResponseEntity<Todo> updateTodoById(Long id, TodoRequest todoRequest) { //transactional
        String description = todoRequest.getDescription();
        LocalDate dueDate = todoRequest.getDueDate();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoDoesNotExistException("Todo with id " + id + " does not exist."));

        if(dueDate!=null && !isGivenDueDateValid(dueDate)) {
            throw new InvalidDueDateException("Given dueDate is not valid.");
        }
        if(userService.getAuthenticatedUserId() != todo.getOwner().getId()) {
            throw new UnauthorizedException("You can not update someone else's todo!");
        }
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        final Todo updatedTodo = todoRepository.save(todo);
        return ResponseEntity.ok(updatedTodo);
    }

    // Bu metod admin için, henüz kullanılabilir değil.
    public List<TodoResponse> getTodosOf(Long userId) {
        if(userService.getAuthenticatedUser().get().getRole() == Role.ADMIN) {
            return convertToResponse(todoRepository.findTodosByOwnerId(userId));
        }
        throw new UnauthorizedException("Unauthorized");
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
