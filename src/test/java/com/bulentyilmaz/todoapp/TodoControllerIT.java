package com.bulentyilmaz.todoapp;

import com.bulentyilmaz.todoapp.helper.TokenHelper;
import com.bulentyilmaz.todoapp.model.request.TodoRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TodoControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenGetTodosWithNoParameter_thenReturnListOfTodoResponsesAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<List<TodoResponse>> response = restTemplate.exchange("/todo", HttpMethod.GET, request,
                new ParameterizedTypeReference<List<TodoResponse>>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals(2, response.getBody().get(0).getUserId());
        assertEquals(2, response.getBody().get(1).getUserId());
        assertEquals(2, response.getBody().get(2).getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenGetTodosWithParameters_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, httpHeaders);
        ResponseEntity<List<TodoResponse>> response = restTemplate.exchange("/todo?description=test1&dueDate=2022-08-11",
                HttpMethod.GET, request,
                new ParameterizedTypeReference<List<TodoResponse>>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("test1", response.getBody().get(0).getDescription());
        assertEquals(LocalDate.of(2022,8,11), response.getBody().get(0).getDueDate());
        assertEquals(2, response.getBody().get(0).getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenAdminToken_whenGetTodos_returnListOfAllTodoResponsesAndResponseIs200() {
        // Given
        String adminToken = TokenHelper.ADMIN_TOKEN_WITH_ID_1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<TodoResponse>> response = restTemplate.exchange("/todo", HttpMethod.GET, request,
                new ParameterizedTypeReference<List<TodoResponse>>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().get(0).getUserId());
        assertEquals(2, response.getBody().get(1).getUserId());
        assertEquals(2, response.getBody().get(2).getUserId());
        assertEquals(3, response.getBody().get(3).getUserId());
        assertEquals(3, response.getBody().get(4).getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenGetTodoByIdWithGivenTokenId_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/1", HttpMethod.GET, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
        assertEquals("test1", response.getBody().getDescription());
        assertEquals(LocalDate.of(2022, 8, 11), response.getBody().getDueDate());
        assertEquals(2, response.getBody().getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenGetTodoByIdWithAnotherId_thenResponseCodeIs401() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, header);
        ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/4", HttpMethod.GET, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenAdminToken_whenGetTodoById_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String adminToken = TokenHelper.ADMIN_TOKEN_WITH_ID_1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/1", HttpMethod.GET, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
        assertEquals("test1", response.getBody().getDescription());
        assertEquals(LocalDate.of(2022, 8, 11), response.getBody().getDueDate());
        assertEquals(2, response.getBody().getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenAdminToken_whenGetTodoByIdWithMissingTodoId_thenResponseCodeIs404() {
        // Given
        String adminToken = TokenHelper.ADMIN_TOKEN_WITH_ID_1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + adminToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/123", HttpMethod.GET, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenTodoRequestAndUserToken_whenAddTodo_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("test6");
        todoRequest.setDueDate(LocalDate.of(2022,8,16));
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, header);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo", HttpMethod.POST, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todoRequest.getDescription(), response.getBody().getDescription());
        assertEquals(todoRequest.getDueDate(), response.getBody().getDueDate());
        assertEquals(2, response.getBody().getUserId());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenAddNewTodoWithInvalidDueDate_thenResponseCodeIs400() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("test1");
        todoRequest.setDueDate(LocalDate.of(2021, 8, 11));
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo", HttpMethod.POST, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenDeleteTodo_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/1", HttpMethod.DELETE, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
        assertEquals("test1", response.getBody().getDescription());
        assertEquals(LocalDate.of(2022, 8, 11), response.getBody().getDueDate());
        assertEquals(2, response.getBody().getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenDeleteTodoWithNonExistingTodoIdParameter_thenResponseCodeIs404() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/123", HttpMethod.DELETE, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserToken_whenDeleteTodoWithSomeoneElsesTodoId_thenResponseCodeIs401() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/4", HttpMethod.DELETE, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserTokenAndTodoRequest_whenUpdateTodo_thenReturnTodoResponseAndResponseCodeIs200() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(LocalDate.of(2022, 9, 11));
        todoRequest.setDescription("test1_changed");
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, header);
        final ResponseEntity<TodoResponse> responsePut = restTemplate.exchange("/todo/1", HttpMethod.PUT, request,
                new ParameterizedTypeReference<TodoResponse>() {});
        final ResponseEntity<TodoResponse> responseGet = restTemplate.exchange("/todo/1", HttpMethod.GET, request,
                new ParameterizedTypeReference<TodoResponse>() {});


        // Then
        assertEquals(HttpStatus.OK, responsePut.getStatusCode());
        assertEquals(HttpStatus.OK, responseGet.getStatusCode());

        assertEquals(1, responseGet.getBody().getId());
        assertEquals(todoRequest.getDueDate(), responseGet.getBody().getDueDate());
        assertEquals(todoRequest.getDescription(), responseGet.getBody().getDescription());
        assertEquals(2, responseGet.getBody().getUserId());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserTokenAndTodoRequestWithInvalidDueDate_whenUpdateTodo_thenResponseCodeIs400() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDueDate(LocalDate.of(2021, 8, 11));
        todoRequest.setDescription("test1_changed");
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/1", HttpMethod.PUT, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserTokenAndTodoRequest_whenUpdateTodoOfNonExistingTodo_thenResponseCodeIs404() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("testChanged");
        todoRequest.setDueDate(LocalDate.of(2022,8,11));
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/123", HttpMethod.PUT, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/todoController/add_users_with_user_role_and_one_admin.sql", "/todoController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/todoController/delete_todos.sql", "/todoController/delete_users.sql"})
    public void givenUserTokenAndTodoRequest_whenUpdateTodoOfAnotherUsers_thenResponseCodeIs401() {
        // Given
        String userToken = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + userToken);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setDescription("testChanged");
        todoRequest.setDueDate(LocalDate.of(2022,8,11));
        JsonNode requestBodyJson = objectMapper.valueToTree(todoRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, headers);
        final ResponseEntity<TodoResponse> response = restTemplate.exchange("/todo/4", HttpMethod.PUT, request,
                new ParameterizedTypeReference<TodoResponse>() {});

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
