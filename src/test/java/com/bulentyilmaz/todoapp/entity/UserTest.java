package com.bulentyilmaz.todoapp.entity;

import com.bulentyilmaz.todoapp.exception.PasswordMismatchException;
import com.bulentyilmaz.todoapp.exception.UnauthorizedException;
import com.bulentyilmaz.todoapp.exception.UserAlreadyExistsException;
import com.bulentyilmaz.todoapp.exception.UserNotFoundException;
import com.bulentyilmaz.todoapp.model.request.LoginRequest;
import com.bulentyilmaz.todoapp.model.request.PasswordRequest;
import com.bulentyilmaz.todoapp.model.request.RegisterRequest;
import com.bulentyilmaz.todoapp.model.request.UpdateUserRequest;
import com.bulentyilmaz.todoapp.model.response.AuthResponse;
import com.bulentyilmaz.todoapp.model.response.UserResponse;
import com.bulentyilmaz.todoapp.repository.UserRepository;
import com.bulentyilmaz.todoapp.security.JwtService;
import com.bulentyilmaz.todoapp.service.AuthService;
import com.bulentyilmaz.todoapp.service.UserService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.cfg.AvailableSettings.USER;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic;

    @BeforeClass
    public static void init() {
        securityContextHolderMockedStatic = mockStatic(SecurityContextHolder.class);
    }

    @AfterClass
    public static void close() {
        securityContextHolderMockedStatic.close();
    }


    @Test
    public void givenUserRequest_whenAuthServiceRegister_thenReturnNothing() {
        // Given (senaryo)
        User user = createUser();

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("userName");
        request.setLastName("userLastName");
        request.setEmail("user@test.com");
        request.setPassword("12345678");

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(userRepository.save(argumentCaptor.capture())).thenReturn(null);
        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um");

        // When
        authService.register(request);

        // Then
        User capturedUser = argumentCaptor.getValue();

        verify(userRepository).save(capturedUser);

        assertNull(capturedUser.getId());
        assertEquals(user.getFirstName(), capturedUser.getFirstName());
        assertEquals(user.getLastName(), capturedUser.getLastName());
        assertEquals(user.getEmail(), capturedUser.getEmail());
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void givenUserRequest_whenAuthServiceRegister_thenUserAlreadyExists() {
        // Given (senaryo)
        User user = createUser();

        RegisterRequest request = new RegisterRequest();
        request.setFirstName("userName");
        request.setLastName("userLastName");
        request.setEmail("user@test.com");
        request.setPassword("12345678");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(user);

        // When
        authService.register(request);

    }

    @Test
    public void canLogin() {
        // When
        User user = createUser();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("12345678");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.createToken("1")).
                thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9." +
                        "eyJpc3MiOiJ0b2RvLWFwcCIsImF1ZCI6InRvZG8tYXBwIiwic3ViIjoiMiIsImlhdCI6MTY1NzIxOTU2MiwiZXhwIjoxNjU4MDgzNTYyfQ." +
                        "6x0Bp0BG-xOttTF-cX4Wfp2BNmVhCr_42W3AHCL7XfcOTUEANGbevvSRPZSjH6GU7PvalWC0-obswB5HnA9hxw");

        // Then
        AuthResponse authResponse = authService.login(loginRequest);

        // Given
        assertEquals(authResponse.getId(), user.getId());
        assertEquals(authResponse.getToken().toString(),"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9." +
                "eyJpc3MiOiJ0b2RvLWFwcCIsImF1ZCI6InRvZG8tYXBwIiwic3ViIjoiMiIsImlhdCI6MTY1NzIxOTU2MiwiZXhwIjoxNjU4MDgzNTYyfQ." +
                "6x0Bp0BG-xOttTF-cX4Wfp2BNmVhCr_42W3AHCL7XfcOTUEANGbevvSRPZSjH6GU7PvalWC0-obswB5HnA9hxw" );

    }

    @Test(expected = UserNotFoundException.class)
    public void givenUserNotPresent_whenLogin_thenThrowsException() {
        // Given
        User user = createUser();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("12345678");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(null); //!!

        // When
        AuthResponse authResponse = authService.login(loginRequest);

        // Then
    }

    @Test(expected = PasswordMismatchException.class)
    public void givenUsersPasswordWrong_whenLogin_thenThrowsException() {
        // Given
        User user = createUser();
        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("12345678");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(false);

        // When
        AuthResponse authResponse = authService.login(loginRequest);
    }

    @Test
    public void givenUserInformationWithAdminRole_whenUserServiceGetUsers_thenReturnUserResponseList() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");

        User user = createUser();
        user.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<User> users = createUserList();
        when(userRepository.findUsers("Test1", "Test1LastName", "test1@test.com", Role.USER.ordinal()))
                .thenReturn(users);
        // When
        List<UserResponse> response =
                (List<UserResponse>) userService.getUsers("Test1", "Test1LastName", "test1@test.com", Role.USER);

        // Then
        assertEquals(response.size(), users.size());
        UserResponse userResponse = response.get(0);
        User searched = users.get(0);

        assertEquals(userResponse.getId(), searched.getId());
        assertEquals(userResponse.getEmail(), searched.getEmail());
        assertEquals(userResponse.getFirstName(), searched.getFirstName());
        assertEquals(userResponse.getLastName(), searched.getLastName());
        assertEquals(userResponse.getRole(), searched.getRole());

    }

    @Test(expected = UnauthorizedException.class)
    public void givenUserInformationWithUserRole_whenUserServiceGetUsers_thenThrowUnauthorizedException() {
        // Given
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");

        User user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<User> users = createUserList();
        // When
        List<UserResponse> response =
                userService.getUsers("Test1", "Test1LastName", "test1@test.com", Role.USER);

        // Then
    }

    @Test
    public void givenUserIdAndUserUpdateRequestWithGivenId_whenUpdateUser_ThenReturnUserResponse() {
        // Given
        Long idToBeChanged = 1L;
        User user = createUser();
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("changedTest");
        updateUserRequest.setLastName("changedTestLastName");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");

        when(userRepository.findById(idToBeChanged)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        ResponseEntity<UserResponse> userResponseResponseEntity = userService.updateUser(idToBeChanged, updateUserRequest);

        // Then
        UserResponse userResponse = userResponseResponseEntity.getBody();

        assertEquals(updateUserRequest.getFirstName(), userResponse.getFirstName());
        assertEquals(updateUserRequest.getLastName(), userResponse.getLastName());
    }

    @Test(expected = UnauthorizedException.class)
    public void givenUserIdAndUserUpdateRequestWithDifferentId_whenUpdateUser_thenThrowsUnauthorizedException() {
        // Given
        Long idToBeChanged = 2L;
        User user = createUser();
        User userToBeChanged = createUser();
        userToBeChanged.setId(2L);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("changedTest");
        updateUserRequest.setLastName("changedTestLastName");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");

        when(userRepository.findById(idToBeChanged)).thenReturn(Optional.of(userToBeChanged));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.updateUser(idToBeChanged, updateUserRequest);
    }

    @Test()
    public void givenUserIdAndUserUpdateRequestWithDifferentIdWithAdminRole_whenUpdateUser_thenReturnUserResponse() {
        // Given
        Long idToBeChanged = 2L;
        User user = createUser();
        user.setRole(Role.ADMIN);

        User userToBeChanged = createUser();
        userToBeChanged.setId(2L);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("changedTest");
        updateUserRequest.setLastName("changedTestLastName");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("1");

        when(userRepository.findById(idToBeChanged)).thenReturn(Optional.of(userToBeChanged));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(userToBeChanged)).thenReturn(userToBeChanged);

        // When
        ResponseEntity<UserResponse> userResponseResponseEntity = userService.updateUser(idToBeChanged, updateUserRequest);

        // Then
        UserResponse userResponse = userResponseResponseEntity.getBody();

        assertEquals(updateUserRequest.getFirstName(), userResponse.getFirstName(), userToBeChanged.getFirstName());
        assertEquals(updateUserRequest.getLastName(), userResponse.getLastName(), userToBeChanged.getLastName());
    }

    @Test()
    public void givenUserIdAndPasswordRequest_whenChangePassword_thenReturnUserResponse() {
        // Given
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("123456789");
        User user = createUser();
        user.setPasswordHash("$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(passwordRequest.getPassword())).
                thenReturn("$2a$10$5k99tvpc.Vi6fua8d9GOyOA2iyIDqgR.HQa1hHn1pZ9ajvdWlt3Um");
        when(userRepository.save(any())).thenReturn(user);

        // When
        ResponseEntity<UserResponse> userResponseResponseEntity = userService.changePassword(1L, passwordRequest);

        // Then
        UserResponse userResponse = userResponseResponseEntity.getBody();
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getFirstName(), userResponse.getFirstName());
        assertEquals(user.getLastName(), userResponse.getLastName());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getRole(), userResponse.getRole());
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

    private List<User> createUserList() {
        List<User> users = new ArrayList<>();
        User u = new User();
        u.setId(2L);
        u.setFirstName("Test1");
        u.setLastName("Test1LastName");
        u.setEmail("test1@test.com");
        u.setPasswordHash("whatANıceHash!");
        u.setRole(Role.USER);
        users.add(u);
        return users;
    }
}
