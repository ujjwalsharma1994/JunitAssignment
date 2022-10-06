package com.assignment.backend.dto;

import com.assignment.backend.model.TransactionHistory;
import com.assignment.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UserTransactionDto {

    private User user;
    private List<TransactionHistory> transactionHistory;
}
