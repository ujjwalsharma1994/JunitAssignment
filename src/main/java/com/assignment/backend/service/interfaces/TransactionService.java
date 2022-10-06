package com.assignment.backend.service.interfaces;

import com.assignment.backend.dto.RestResponse;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    ResponseEntity<RestResponse> showCurrentBalance(int userId);

    ResponseEntity<RestResponse> addMoneyToAccount(int userId, double balance);

    ResponseEntity<RestResponse> withdrawMoneyFromAccount(int userId, double balance);

    ResponseEntity<RestResponse> showUserTransactionHistory(int userId);
}
