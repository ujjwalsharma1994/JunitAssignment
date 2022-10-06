package com.assignment.backend.service.impl;

import static com.assignment.backend.util.Constants.ACTIVITY_MONEY_DEPOSITED;
import static com.assignment.backend.util.Constants.ACTIVITY_MONEY_WITHDRAWN;
import static com.assignment.backend.util.Constants.STATUS_SUCCESSFUL;
import static com.assignment.backend.util.Constants.STATUS_UNSUCCESSFUL;
import static com.assignment.backend.util.Constants.STATUS_SUCCESSFUL_WITH_PENALTY;

import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.dto.UserTransactionDto;
import com.assignment.backend.model.TransactionHistory;
import com.assignment.backend.model.User;
import com.assignment.backend.repository.TransactionHistoryRepository;
import com.assignment.backend.repository.UserRepository;
import com.assignment.backend.service.interfaces.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionHistoryRepository transactionRepository;
    @Override
    public ResponseEntity<RestResponse> showCurrentBalance(int userId) {

        if (userId > 0) {

            Optional<User> userOptional = userRepository.findById(userId);
            var resultBalance = userOptional.map(User::getBalance)
                    .orElse(new BigDecimal(0));

            if (resultBalance.doubleValue() > 500) { // if user has sufficient funds
                return ResponseEntity.status(200).body(new RestResponse(200,
                        null, "Your Current Balance :: " + resultBalance.doubleValue(),
                        userOptional.orElse(null)));
            } else {
                return userOptional.map(user -> ResponseEntity.status(200).body(new RestResponse(200,
                        "Your current balance is very low. Please add money to avoid penalty charges.",
                        "Your Current Balance :: " + resultBalance.doubleValue(),
                        user))).orElseGet(() -> generate404ResponseTemplate(userId));
            }
        } else {
            return generate400ResponseTemplate(userId);
        }
    }

    @Override
    public ResponseEntity<RestResponse> addMoneyToAccount(int userId, double amount) {

        if (userId > 0) {
            if (amount > 1) {
                Optional<User> userOptional = userRepository.findById(userId);
                return userOptional.map(user -> {
                            if (user.getBalance().doubleValue() > 500) { // check if user current balance is more than minimum balance of 500.

                                user.setBalance(user.getBalance().add(new BigDecimal(amount)));
                                userRepository.save(user);
                                this.saveUserTransactionRecord(userId, new BigDecimal(amount), ACTIVITY_MONEY_DEPOSITED, STATUS_SUCCESSFUL);
                                return ResponseEntity
                                        .ok()
                                        .body(new RestResponse(200,
                                        null,
                                        "Added money successfully.\n" +
                                                "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                        user));
                            } else { // then reduce the balance by charging penalty of 500 and then add the balance.
                                BigDecimal finalAmount = user.getBalance().add(new BigDecimal(amount)).subtract(new BigDecimal(500));
                                user.setBalance(finalAmount);
                                this.saveUserTransactionRecord(userId, new BigDecimal(amount), ACTIVITY_MONEY_DEPOSITED, STATUS_SUCCESSFUL_WITH_PENALTY);
                                userRepository.save(user);
                                return ResponseEntity
                                        .ok()
                                        .body(new RestResponse(200,
                                        "You have very low balance. So we deducted 500 " +user.getCurrency()+ ". amount as penalty.",
                                        "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                        user));
                            }
                        }).orElseGet(() -> generate404ResponseTemplate(userId));
            } else {
                return ResponseEntity
                        .status(400)
                        .body(new RestResponse(400,
                                "bad request : please enter amount more than 1.",
                                null, userId));
            }
        } else {
            return generate400ResponseTemplate(userId);
        }
    }

    @Override
    public ResponseEntity<RestResponse> withdrawMoneyFromAccount(int userId, double amount) {
        if (userId > 0) {
            if (amount > 1) {
                Optional<User> userOptional = userRepository.findById(userId);
                return userOptional.map(user -> {
                    if (user.getBalance().doubleValue() > 500) { // check if user current balance is more than minimum balance of 500.

                        var balanceRemaining = user.getBalance().subtract(new BigDecimal(amount));

                        if (balanceRemaining.longValue() > 500) {
                            user.setBalance(balanceRemaining);
                            this.saveUserTransactionRecord(userId, new BigDecimal(amount), ACTIVITY_MONEY_WITHDRAWN, STATUS_SUCCESSFUL);
                            userRepository.save(user);
                            return ResponseEntity
                                    .ok()
                                    .body(new RestResponse(200,
                                            null,
                                            "Money withdrawn successfully.\n" +
                                                    "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                            user));
                        } else if (balanceRemaining.longValue() > 0 && balanceRemaining.longValue() < 500) {
                            user.setBalance(balanceRemaining);
                            this.saveUserTransactionRecord(userId, new BigDecimal(amount), ACTIVITY_MONEY_WITHDRAWN, STATUS_SUCCESSFUL_WITH_PENALTY);
                            userRepository.save(user);
                            return ResponseEntity
                                    .ok()
                                    .body(new RestResponse(200,
                                            "Your current balance is less than 500"+ user.getCurrency() +". Please add money as soon as possible to avoid penalty charges.",
                                            "Money withdrawn successfully.\n" +
                                                    "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                            user));
                        } else {
                            return ResponseEntity
                                    .status(400)
                                    .body(new RestResponse(400,
                                            "Your current balance is less than the withdrawal amount " + amount + " " + user.getCurrency() +
                                                    ". Withdrawal is possible when current balance is more than withdrawal amount. " +
                                                    "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                            null,
                                            user));
                        }
                    } else {
                        this.saveUserTransactionRecord(userId, new BigDecimal(amount), ACTIVITY_MONEY_WITHDRAWN, STATUS_UNSUCCESSFUL);
                        return ResponseEntity
                                .ok()
                                .body(new RestResponse(400,
                                        "You have very low balance. So we cannot make this transaction for now. " +
                                                "Your Current Balance is :: " + user.getBalance().doubleValue(),
                                        null,
                                        user));
                    }
                }).orElseGet(() -> generate404ResponseTemplate(userId));
            } else {
                return ResponseEntity
                        .status(400)
                        .body(new RestResponse(400,
                                "bad request : please enter amount more than 1.",
                                null, userId));
            }
        } else {
            return generate400ResponseTemplate(userId);
        }
    }

    @Override
    public ResponseEntity<RestResponse> showUserTransactionHistory(int userId) {

        if (userId > 0) {
            List<TransactionHistory> transactionHistories = transactionRepository.findByUserId(userId);

            return Optional.ofNullable(transactionHistories).map(transactionHistory -> {
                Optional<User> user = userRepository.findById(userId);
                UserTransactionDto userTransactionDto = new UserTransactionDto(user.orElse(null), transactionHistory);
                return ResponseEntity
                        .ok()
                        .body(new RestResponse(200,
                                null,
                                "found "+transactionHistories.size()+ " transactions for user id :: " + userId,
                                userTransactionDto));
            }).orElseGet(() -> generate404ResponseTemplate(userId));

        } else {
            return generate400ResponseTemplate(userId);
        }
    }

    public void saveUserTransactionRecord(int userId, BigDecimal amount, String activity, String status) {

        TransactionHistory transactionHistory = new TransactionHistory();

        transactionHistory.setUserId(userId);
        transactionHistory.setActivity(activity);
        transactionHistory.setTransactionAmount(amount);
        transactionHistory.setStatus(status);

        transactionRepository.save(transactionHistory);
    }

    public static ResponseEntity<RestResponse> generate404ResponseTemplate(int userId) {
        return ResponseEntity.status(404).body(new RestResponse(404,
                "No User found for the id :: " + userId,
                null,
                userId));
    }

    public static ResponseEntity<RestResponse> generate400ResponseTemplate(int userId) {
        return ResponseEntity.status(400).body(new RestResponse(400,
                "bad request : please input correct user id.",
                null,
                userId));
    }
}
