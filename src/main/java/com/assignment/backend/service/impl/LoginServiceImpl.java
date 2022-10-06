package com.assignment.backend.service.impl;

import static java.util.Objects.nonNull;

import com.assignment.backend.dto.LoginRequest;
import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.dto.UserRequest;
import com.assignment.backend.model.User;
import com.assignment.backend.repository.UserRepository;
import com.assignment.backend.service.interfaces.LoginService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<RestResponse> signUpUser(UserRequest userRequest) {

        if (nonNull(userRequest)) {

            if (nonNull(userRequest.getEmail()) && nonNull(userRequest.getName())) {
                Gson gson= new Gson();
                String userString = gson.toJson(userRequest);
                User userObject = gson.fromJson(userString, User.class);
                userRepository.save(userObject);
                return ResponseEntity.status(201).body(new RestResponse(201,
                        null, "User signed up successfully", userObject));
            } else {
                return ResponseEntity
                        .status(400)
                        .body(new RestResponse(400,
                                "bad request : please input correct email or name.",
                                null, userRequest));
            }
        } else {
            return ResponseEntity
                    .status(400)
                    .body(new RestResponse(400,
                            "bad request : please input correct info.",
                            null, null));
        }
    }

    @Override
    public ResponseEntity<RestResponse> loginUser(LoginRequest loginRequest) {

        if (nonNull(loginRequest.getEmail()) && nonNull(loginRequest.getPassword())
                && !loginRequest.getEmail().isBlank() && !loginRequest.getPassword().isBlank()) {

            Optional<User> userFound = userRepository.findByEmail(loginRequest.getEmail());

            var result = userFound.map(User::getPassword)
                    .map(password -> password.equals(loginRequest.getPassword()))
                    .filter(e -> e.equals(true))
                    .orElse(false);

            if (result) {
                return ResponseEntity.status(201).body(new RestResponse(201,
                        null, "User logged in successfully", userFound.orElse(null)));
            } else {
                return ResponseEntity.status(401).body(new RestResponse(401,
                        "Wrong email/password provided", null, loginRequest));
            }
        } else {
            return ResponseEntity
                    .status(400)
                    .body(new RestResponse(400,
                            "bad request : please input correct info.",
                            null, loginRequest));
        }
    }
}
