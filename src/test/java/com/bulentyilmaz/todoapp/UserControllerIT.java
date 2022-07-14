package com.bulentyilmaz.todoapp;

import com.bulentyilmaz.todoapp.entity.Role;
import com.bulentyilmaz.todoapp.helper.TokenHelper;
import com.bulentyilmaz.todoapp.model.request.UpdateUserRequest;
import com.bulentyilmaz.todoapp.model.response.TodoResponse;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserControllerIT {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    value = "/userController/add_users_with_user_role_and_one_admin.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/userController/delete_all_users.sql")
    public void givenAdminToken_whenGetUsers_thenReturnListOfUserResponsesAndStatusCodeIs200() {
        // Given
        String adminToken = TokenHelper.ADMIN_TOKEN_WITH_ID_1;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", "Bearer " + adminToken);

        // When
        final HttpEntity<String> request = new HttpEntity<>(requestHeaders);
        ResponseEntity<List<UserResponse>> response = restTemplate.exchange("/user",
                HttpMethod.GET,
                request, new ParameterizedTypeReference<List<UserResponse>>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test1@test.com", response.getBody().get(0).getEmail());
        assertEquals("test2@test.com", response.getBody().get(1).getEmail());
        assertEquals("test3@test.com", response.getBody().get(2).getEmail());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    value = "/userController/add_users_with_user_role_and_one_admin.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/userController/delete_all_users.sql")
    public void givenUserToken_whenGetUser_returnUserResponseAndResponseCodeIs200() {
        // Given
        HttpHeaders httpHeaders = new HttpHeaders();
        String token = TokenHelper.USER_TOKEN_WITH_ID_2;
        httpHeaders.add("Authorization", "Bearer " + token);

        // When
        final HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        final ResponseEntity<UserResponse> response = restTemplate.exchange("/user/me", HttpMethod.GET,
                request, new ParameterizedTypeReference<>(){});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getId());
        assertEquals("test2@test.com", response.getBody().getEmail());
        assertEquals("test2", response.getBody().getFirstName());
        assertEquals("test2LastName", response.getBody().getLastName());
        assertEquals(Role.USER.ordinal(), response.getBody().getRole().ordinal());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = "/userController/add_users_with_user_role_and_one_admin.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/userController/delete_all_users.sql")
    public void givenUserToken_whenUpdateUserAndGetUser_thenResponsesCodeAre200AndUserUpdated() {
        // Given
        String token = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("updated");
        updateUserRequest.setLastName("updatedLastName");
        JsonNode requestBodyJson = objectMapper.valueToTree(updateUserRequest);

        // When
        final HttpEntity<JsonNode> request_put = new HttpEntity<>(requestBodyJson, httpHeaders);
        final HttpEntity<String> request_get = new HttpEntity<>(httpHeaders);
        final ResponseEntity<UserResponse> response_put = restTemplate.exchange("/user/me", HttpMethod.PUT,
                request_put, new ParameterizedTypeReference<>(){});
        final ResponseEntity<UserResponse> response_get = restTemplate.exchange("/user/me", HttpMethod.GET, request_get,
                new ParameterizedTypeReference<UserResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response_put.getStatusCode());
        assertEquals(HttpStatus.OK, response_get.getStatusCode());
        assertEquals(updateUserRequest.getFirstName(), response_get.getBody().getFirstName());
        assertEquals(updateUserRequest.getLastName(), response_get.getBody().getLastName());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = {"/userController/add_users_with_user_role_and_one_admin.sql", "/userController/add_todos.sql"})
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = {"/userController/delete_all_todos.sql", "/userController/delete_all_users.sql"})
    public void givenAdminToken_whenGetTodosOf_thenReturnTodosOfUserAndResponseCodeIs200() throws JsonProcessingException {
        // Given
        String token = TokenHelper.ADMIN_TOKEN_WITH_ID_1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        // When
        final HttpEntity<String> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<TodoResponse>> response = restTemplate.exchange("/user/2/todo", HttpMethod.GET, request,
                new ParameterizedTypeReference<>() {});
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().get(0).getUserId());
        assertEquals(2, response.getBody().get(1).getUserId());
        assertEquals(2, response.getBody().get(2).getUserId());
    }
}
