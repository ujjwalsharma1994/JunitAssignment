package com.assignment.backend.service.interfaces;

import com.assignment.backend.dto.LoginRequest;
import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.dto.UserRequest;
import org.springframework.http.ResponseEntity;

public interface LoginService {

    ResponseEntity<RestResponse> loginUser(LoginRequest loginRequest);

    ResponseEntity<RestResponse> signUpUser(UserRequest userRequest);
}
