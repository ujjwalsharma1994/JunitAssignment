package com.assignment.backend.controller;

import com.assignment.backend.dto.LoginRequest;
import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.dto.UserRequest;
import com.assignment.backend.service.interfaces.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private LoginService loginService;

    @PostMapping(path = "/login")
    public ResponseEntity<RestResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return loginService.loginUser(loginRequest);
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<RestResponse> signUpUser(@RequestBody UserRequest userRequest) {
        return loginService.signUpUser(userRequest);
    }
}
