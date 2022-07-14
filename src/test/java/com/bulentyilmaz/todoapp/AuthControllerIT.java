package com.bulentyilmaz.todoapp;

import com.bulentyilmaz.todoapp.helper.TokenHelper;
import com.bulentyilmaz.todoapp.model.request.LoginRequest;
import com.bulentyilmaz.todoapp.model.request.PasswordRequest;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.response.AuthResponse;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AuthControllerIT
{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = "/authcontroller/remove_user_for_create_user_test.sql")
    public void givenNewUserInformation_whenAuthControllerRegister_thenStatusCode200() {
        // Given
        HttpHeaders requestHeaders = new HttpHeaders();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("TestLastName");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("12345678");
        JsonNode requestBodyJson = objectMapper.valueToTree(registerRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, requestHeaders);
        final ResponseEntity<RegisterRequest> response = restTemplate.exchange("/auth/register",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<RegisterRequest>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = "/authcontroller/add_user_with_user_role.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = "/authcontroller/remove_user_for_create_user_test.sql")
    public void givenUserRegistered_whenUserLogin_thenLoginAndStatusCode200() {
        // Given
        HttpHeaders requestHeaders = new HttpHeaders();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("12345678");
        JsonNode requestBodyJson = objectMapper.valueToTree(loginRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, requestHeaders);
        final ResponseEntity<AuthResponse> response = restTemplate.exchange("/auth/login",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<AuthResponse>() {
                });

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
    }

    @Test()
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = "/authcontroller/add_user_with_user_role.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = "/authcontroller/remove_user_for_create_user_test.sql")
    public void givenExistingUserInformation_whenLogin_thenThrowExceptionAndStatusCodeIs409() {
        // Given
        HttpHeaders requestHeaders = new HttpHeaders();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("TestLastName");
        registerRequest.setEmail("test@test.com");
        registerRequest.setPassword("12345678");
        JsonNode requestBodyJson = objectMapper.valueToTree(registerRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, requestHeaders);
        final ResponseEntity<RegisterRequest> response = restTemplate.exchange("/auth/register",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<RegisterRequest>() {
                });

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            value = "/authcontroller/add_user_with_user_role.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = "/authcontroller/remove_user_for_create_user_test.sql")
    public void givenNonExistingUserInformation_whenLogin_thenResponseCodeIs404() {
        // Given
        HttpHeaders requestHeaders = new HttpHeaders();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testt@test.com");
        loginRequest.setPassword("12345678");
        JsonNode requestBodyJson = objectMapper.valueToTree(loginRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, requestHeaders);
        final ResponseEntity<AuthResponse> response = restTemplate.exchange("/auth/login",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<AuthResponse>() {
                });

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    value = "/authcontroller/add_user_with_user_role.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
            value = "/authcontroller/remove_user_for_create_user_test.sql")
    public void givenUserWithWrongPassword_whenLogin_thenResponseCodeIs401() {
        // Given
        HttpHeaders requestHeaders = new HttpHeaders();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("12345678asd");
        JsonNode requestBodyJson = objectMapper.valueToTree(loginRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, requestHeaders);
        final ResponseEntity<AuthResponse> response = restTemplate.exchange("/auth/login",
                HttpMethod.POST,
                request, new ParameterizedTypeReference<AuthResponse>() {
                });

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/userController/add_users_with_user_role_and_one_admin.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/userController/delete_all_users.sql")
    public void givenUserToken_whenChangePassword_thenChangePasswordAndResponseCodeIs200() {
        // Given
        String token = TokenHelper.USER_TOKEN_WITH_ID_2;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("123456789");
        JsonNode requestBodyJson = objectMapper.valueToTree(passwordRequest);

        // When
        final HttpEntity<JsonNode> request = new HttpEntity<>(requestBodyJson, headers);
        final ResponseEntity<UserResponse> response = restTemplate.exchange("/auth/me/pass", HttpMethod.PUT, request,
                new ParameterizedTypeReference<UserResponse>() {});

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
