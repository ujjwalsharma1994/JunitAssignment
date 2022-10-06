package com.assignment.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class UserRequest {

    private int userId;
    private String email;
    private String password;
    private String name;
    private LocalDate birthDate;
    private double balance;
    private String currency;
}