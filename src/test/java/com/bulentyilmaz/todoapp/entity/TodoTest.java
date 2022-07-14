package com.bulentyilmaz.todoapp.entity;

import com.bulentyilmaz.todoapp.exception.InvalidDueDateException;
import com.bulentyilmaz.todoapp.exception.TodoDoesNotExistException;
import com.bulentyilmaz.todoapp.exception.UnauthorizedException;
import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.repository.TodoRepository;
import com.bulentyilmaz.todoapp.service.TodoService;
import com.bulentyilmaz.todoapp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TodoTest {

    @InjectMocks
    TodoService todoService;

    @Mock
    UserService userService;

    @Mock
    TodoRepository todoRepository;

    @Test
    public void canAddNewTodo() {
        // Given
        User user = createUser();
        Optional<User> opUser = Optional.of(user);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("test");
        todoRequest.setDueDate(null);

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription(todoRequest.getDescription());
        todo.setDueDate(todoRequest.getDueDate());
        todo.setOwner(user);

        ArgumentCaptor<Todo> argumentCaptor = ArgumentCaptor.forClass(Todo.class);

        when(userService.getAuthenticatedUser()).thenReturn(opUser);
        when(todoRepository.save(argumentCaptor.capture())).thenReturn(todo);

        // When
        TodoResponse todoResponse = todoService.addNewTodo(todoRequest);

        // Then
        verify(todoRepository).save(argumentCaptor.capture());
        Todo todoCaptured = argumentCaptor.getValue();

        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todoCaptured.getDueDate(), todoResponse.getDueDate());
        assertEquals(todoCaptured.getDescription(), todoResponse.getDescription());
        assertEquals(todo.getOwner().getId(), todoResponse.getUserId());
    }

    @Test(expected = InvalidDueDateException.class)
    public void givenTodoRequestWithInvalidDueDate_whenAddTodo_thenThrowInvalidDueDateException() {
        // Given
        User user = createUser();
        Optional<User> opUser = Optional.of(user);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("test");
        todoRequest.setDueDate(LocalDate.of(2021,11,11));

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription(todoRequest.getDescription());
        todo.setDueDate(todoRequest.getDueDate());
        todo.setOwner(user);

        // When
        todoService.addNewTodo(todoRequest);
    }

    @Test
    public void givenDescriptionAndDueDateWithUserRole_whenGetTodos_thenReturnListOfTodoResponse() {
        // Given
        String description = "testTodo";
        LocalDate dueDate = null;

        User user = createUser();
        Optional<User> optionalUser = Optional.of(user);

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("testTodo");
        todo.setDueDate(null);
        todo.setOwner(user);

        List<Todo> todos = new ArrayList<>();
        todos.add(todo);

        when(userService.getAuthenticatedUser()).thenReturn(optionalUser);
        when(userService.getAuthenticatedUserId()).thenReturn(1L);
        when(todoRepository.findTodos(description, dueDate, 1L)).thenReturn(todos);

        // When
        List<TodoResponse> todoResponseList = todoService.getTodos(description, dueDate);

        // Then
        assertEquals(1, todoResponseList.size());
        assertEquals(todo.getDescription(),todoResponseList.get(0).getDescription()) ;
        assertEquals(todo.getDueDate(), todoResponseList.get(0).getDueDate());
        assertEquals(todo.getId(), todoResponseList.get(0).getId());
        assertEquals(todo.getOwner().getId(), todoResponseList.get(0).getUserId());
    }

    @Test
    public void givenDescriptionAndDueDateWithAdminRole_whenGetTodos_thenReturnListOfTodoResponse() {
        // Given
        String description = "testTodo";
        LocalDate dueDate = null;

        User user = createUser();
        user.setRole(Role.ADMIN);
        Optional<User> optionalUser = Optional.of(user);

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("testTodo");
        todo.setDueDate(null);
        todo.setOwner(user);

        List<Todo> todos = new ArrayList<>();
        todos.add(todo);

        when(userService.getAuthenticatedUser()).thenReturn(optionalUser);
        when(todoRepository.findTodos(description, dueDate, null)).thenReturn(todos);

        // When
        List<TodoResponse> todoResponseList = todoService.getTodos(description, dueDate);

        // Then
        assertEquals(1, todoResponseList.size());
        assertEquals(todo.getDescription(),todoResponseList.get(0).getDescription()) ;
        assertEquals(todo.getDueDate(), todoResponseList.get(0).getDueDate());
        assertEquals(todo.getId(), todoResponseList.get(0).getId());
        assertEquals(todo.getOwner().getId(), todoResponseList.get(0).getUserId());
    }

    @Test
    public void givenTodoId_whenGetTodoById_thenReturnTodoResponse() {
        // Given
        Long todoId = 1L;
        User user = createUser();

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("testTodo");
        todo.setDueDate(null);
        todo.setOwner(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(1L);
        // When
        TodoResponse todoResponse = todoService.getTodoById(todoId);

        // Then
        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todo.getDescription(), todoResponse.getDescription());
        assertEquals(todo.getDueDate(), todoResponse.getDueDate());
        assertEquals(todo.getOwner().getId(), todoResponse.getUserId());
    }

    @Test
    public void givenTodoIdButDifferentUserWithIdAndWithRoleAdmin_whenGetTodoById_thenReturnTodoResponse() {
        // Given
        Long todoId = 1L;
        User user = createUser();
        user.setId(2L);
        user.setRole(Role.ADMIN);

        User owner = createUser();

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("testTodo");
        todo.setDueDate(null);
        todo.setOwner(owner);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(2L);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(user));

        // When
        TodoResponse todoResponse = todoService.getTodoById(todoId);

        // Then
        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todo.getDescription(), todoResponse.getDescription());
        assertEquals(todo.getDueDate(), todoResponse.getDueDate());
        assertEquals(todo.getOwner().getId(), todoResponse.getUserId());
    }

    @Test(expected = UnauthorizedException.class)
    public void givenTodoIdButDifferentUserWithIdAndWithRoleUser_whenGetTodoById_thenThrowUnauthorizedException() {
        // Given
        Long todoId = 1L;
        User user = createUser();
        user.setId(2L);

        User owner = createUser();

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("testTodo");
        todo.setDueDate(null);
        todo.setOwner(owner);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(2L);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(user));

        // When
        TodoResponse todoResponse = todoService.getTodoById(todoId);
    }

    @Test(expected = TodoDoesNotExistException.class)
    public void givenTodoId_whenGetTodoById_thenThrowTodoDoesNotExistException() {
        // Given
        Long todoId = 1L;
        User user = createUser();

        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // When
        todoService.getTodoById(todoId);
    }

    @Test
    public void givenTodoIdAndAuthenticatedUser_whenDeleteTodo_thenReturnTodoResponse() {
        // Given
        User user = createUser();
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("test");
        todo.setDueDate(null);
        todo.setOwner(user);

        when(todoRepository.existsById(1L)).thenReturn(true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(1L);
        doNothing().when(todoRepository).deleteById(1L);

        // When
        TodoResponse todoResponse = todoService.deleteTodo(1L);

        // Then
        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todo.getDescription(), todoResponse.getDescription());
        assertEquals(todo.getDueDate(), todoResponse.getDueDate());
        assertEquals(todo.getOwner().getId(), todoResponse.getUserId());
    }

    @Test(expected = TodoDoesNotExistException.class)
    public void givenNotExistingTodoIdAndAuthenticatedUser_whenDeleteTodo_thenThrowTodoDoesNotExistException() {
        // Given
        User user = createUser();
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("test");
        todo.setDueDate(null);
        todo.setOwner(user);

        when(todoRepository.existsById(1L)).thenReturn(false);

        // When
        TodoResponse todoResponse = todoService.deleteTodo(1L);
    }

    @Test(expected = UnauthorizedException.class)
    public void givenTodoIdAndAuthenticatedUserNotOwnerOfGivenTodo_whenDeleteTodo_thenThrowUnauthorizedException() {
        // Given
        User user = createUser();
        User owner = createUser();
        owner.setId(2L);
        owner.setEmail("user2@test.com");
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("test");
        todo.setDueDate(null);
        todo.setOwner(owner);

        when(todoRepository.existsById(1L)).thenReturn(true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(1L);

        // When
        TodoResponse todoResponse = todoService.deleteTodo(1L);
    }

    @Test
    public void givenTodoIdAndTodoRequest_whenUpdateTodo_thenReturnUpdatedTodo() {
        // Given
        User user = createUser();

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(null);
        todoRequest.setDescription("changedTest");
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDueDate(null);
        todo.setDescription("test");
        todo.setOwner(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(1L);
        when(todoRepository.save(todo)).thenReturn(todo);

        // When
        ResponseEntity<TodoResponse> todoResponseResponseEntity = todoService.updateTodoById(todoId, todoRequest);

        // Then
        TodoResponse todoResponse = todoResponseResponseEntity.getBody();

        assertEquals(todo.getId(), todoResponse.getId());
        assertEquals(todoRequest.getDescription(), todoResponse.getDescription());
        assertEquals(todoRequest.getDueDate(), todoResponse.getDueDate());
        assertEquals(todo.getOwner().getId(), todoResponse.getUserId());
    }

    @Test(expected = TodoDoesNotExistException.class)
    public void givenNonExistingTodoIdAndTodoRequest_whenUpdateTodo_thenThrowTodoDoesNotExistException() {
        // Given
        User user = createUser();

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(null);
        todoRequest.setDescription("changedTest");
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDueDate(null);
        todo.setDescription("test");
        todo.setOwner(user);

        // When
        todoService.updateTodoById(todoId, todoRequest);
    }

    @Test(expected = UnauthorizedException.class)
    public void givenTodoIdAndTodoRequestAndUnauthorizedUser_whenUpdateTodo_thenThrowUnauthorizedException() {
        // Given
        User user = createUser();

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(null);
        todoRequest.setDescription("changedTest");
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDueDate(null);
        todo.setDescription("test");
        todo.setOwner(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(userService.getAuthenticatedUserId()).thenReturn(2L);

        // When
        todoService.updateTodoById(todoId, todoRequest);
    }

    @Test(expected = InvalidDueDateException.class)
    public void givenTodoIdAndTodoRequestWithInvalidDueDate_whenUpdateTodo_thenThrowInvalidDueDateException() {
        // Given
        User user = createUser();

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(LocalDate.of(2021,11,11));
        todoRequest.setDescription("changedTest");
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDueDate(null);
        todo.setDescription("test");
        todo.setOwner(user);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        // When
        ResponseEntity<TodoResponse> todoResponseResponseEntity = todoService.updateTodoById(todoId, todoRequest);
    }

    @Test
    public void givenUserIdAndUserWithAdminRole_whenGetTodosOf_thenReturnListOfTodoResponse() {
        // Given
        User user = createUser();
        user.setRole(Role.ADMIN);

        User owner = createUser();
        owner.setId(2L);
        owner.setEmail("user2@test.com");

        List<Todo> todoList = new ArrayList<>();
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("Test");
        todo.setDueDate(null);
        todo.setOwner(owner);
        todoList.add(todo);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(user));
        when(todoRepository.findTodosByOwnerId(2L)).thenReturn(todoList);

        // When
        List<TodoResponse> todoResponseList = todoService.getTodosOf(2L);

        // Then
        assertEquals(todoResponseList.size(), 1);
        assertEquals(todo.getId(), todoResponseList.get(0).getId());
        assertEquals(todo.getDescription(), todoResponseList.get(0).getDescription());
        assertEquals(todo.getDueDate(), todoResponseList.get(0).getDueDate());
        assertEquals(todo.getOwner().getId(), todoResponseList.get(0).getUserId());

    }

    @Test(expected = UnauthorizedException.class)
    public void givenUserIdAndUserWithUserRole_whenGetTodosOf_thenThrowUnauthorizedException() {
        // Given
        User user = createUser();

        User owner = createUser();
        owner.setId(2L);
        owner.setEmail("user2@test.com");

        List<Todo> todoList = new ArrayList<>();
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setDescription("Test");
        todo.setDueDate(null);
        todo.setOwner(owner);
        todoList.add(todo);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(user));
        // When
        List<TodoResponse> todoResponseList = todoService.getTodosOf(2L);
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("userName");
        user.setLastName("userLastName");
        user.setEmail("user@test.com");
        user.setPasswordHash("$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um");
        user.setRole(Role.USER);

        return user;
    }

}
