package com.assignment.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int userId;
    private String name;
    private String email;
    private String password;
    private LocalDate birthDate;
    private BigDecimal balance;
    private String currency;
    private int nextLineId;

    public User(int userId, String name, String email, String password, LocalDate birthDate, BigDecimal balance, String currency) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.balance = balance;
        this.currency = currency;
    }
}
