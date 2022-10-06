package com.assignment.backend.util;

import com.assignment.backend.dto.UserRequest;
import com.assignment.backend.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class UserUtil {
    public static Optional<User> createDummyUser() {
        User dummyUser = new User(1, "Ujjwal", "ujjwal@gmail.com", "xyz",
                LocalDate.now(), new BigDecimal(2000), "INR");
        return Optional.of(dummyUser);
    }

    public static Optional<User> createDummyUserWithInsufficientBalance() {
        User dummyUser = new User(1, "Ujjwal", "ujjwal@gmail.com", "xyz",
                LocalDate.now(), new BigDecimal("499.5"), "INR");
        return Optional.of(dummyUser);
    }

    public static UserRequest createUserRequest() {

        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(1);
        userRequest.setBalance(1000.0);
        userRequest.setName("John Doe");
        userRequest.setEmail("johnDoe@gmail.com");
        userRequest.setPassword("abc@1234");
        userRequest.setBirthDate(LocalDate.of(1993, 7, 29));
        userRequest.setCurrency("INR");

        return userRequest;
    }

    public static UserRequest createUserRequestWithoutEmail() {

        UserRequest userRequest = new UserRequest();
        userRequest.setUserId(1);
        userRequest.setBalance(1000.0);
        userRequest.setName("John Doe");
        userRequest.setPassword("abc@1234");
        userRequest.setBirthDate(LocalDate.of(1993, 7, 29));
        userRequest.setCurrency("INR");

        return userRequest;
    }
}
