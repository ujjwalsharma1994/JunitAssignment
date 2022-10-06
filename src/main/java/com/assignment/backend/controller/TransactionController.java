package com.assignment.backend.controller;

import com.assignment.backend.dao.UserDao;
import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserDao userDao;

    @GetMapping(path = "/currentBalance")
    public ResponseEntity<RestResponse> showCurrentBalance(@RequestParam(name = "userId") int userId) {
        return transactionService.showCurrentBalance(userId);
    }

    @GetMapping(path = "/addMoney")
    public ResponseEntity<RestResponse> addMoneyToAccount(@RequestParam(name = "userId") int userId,
                                                          @RequestParam(name = "amount") double balance) {
        return transactionService.addMoneyToAccount(userId, balance);
    }

    @GetMapping(path = "/withdrawMoney")
    public ResponseEntity<RestResponse> withdrawMoneyFromAccount(@RequestParam(name = "userId") int userId,
                                                                 @RequestParam(name = "amount") double amount) {
        return transactionService.withdrawMoneyFromAccount(userId, amount);
    }

    @GetMapping(path = "/history")
    public ResponseEntity<RestResponse> showTransactionHistoryOfUser(@RequestParam(name = "userId") int userId) {
        return transactionService.showUserTransactionHistory(userId);
    }
}
