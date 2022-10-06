package com.assignment.backend.service;

import static com.assignment.backend.util.UserUtil.*;

import com.assignment.backend.dto.LoginRequest;
import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.repository.UserRepository;
import com.assignment.backend.service.impl.LoginServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    public void init() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void login_user_with_incorrect_password() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("abc");
        loginRequest.setEmail("ujjwal@1234");

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(createDummyUser());

        ResponseEntity<RestResponse> response = loginService.loginUser(loginRequest);
        Assertions.assertEquals(401, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("Wrong email/password provided",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        Assertions.assertNull(response.getBody().getSuccessMessage());

    }

    @Test
    public void login_user_with_correct_password() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("xyz");
        loginRequest.setEmail("ujjwal@1234");

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(createDummyUser());

        ResponseEntity<RestResponse> response = loginService.loginUser(loginRequest);
        Assertions.assertEquals(201, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("User logged in successfully",
                Objects.requireNonNull(response.getBody()).getSuccessMessage());
        Assertions.assertNull(response.getBody().getErrorMessage());
        Mockito.verify(userRepository).findByEmail(loginRequest.getEmail());
    }

    @Test
    public void login_user_with_no_email_and_password() {

        LoginRequest loginRequest = new LoginRequest();
        ResponseEntity<RestResponse> response = loginService.loginUser(loginRequest);
        Assertions.assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("bad request : please input correct info.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        Assertions.assertNull(response.getBody().getSuccessMessage());

    }

    @Test
    public void signup_user_will_return_200() {

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(createDummyUser().orElse(null));
        ResponseEntity<RestResponse> response = loginService.signUpUser(createUserRequest());

        Assertions.assertEquals(201, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("User signed up successfully",
                Objects.requireNonNull(response.getBody()).getSuccessMessage());
        Assertions.assertNull(response.getBody().getErrorMessage());
    }

    @Test
    public void signup_user_will_return_400_as_email_is_not_provided() {

        ResponseEntity<RestResponse> response = loginService.signUpUser(createUserRequestWithoutEmail());

        Assertions.assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("bad request : please input correct email or name.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        Assertions.assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void signup_user_will_return_400_as_user_request_is_not_provided() {

        ResponseEntity<RestResponse> response = loginService.signUpUser(null);

        Assertions.assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        Assertions.assertEquals("bad request : please input correct info.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        Assertions.assertNull(response.getBody().getSuccessMessage());
    }
}
