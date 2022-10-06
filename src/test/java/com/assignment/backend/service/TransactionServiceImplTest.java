package com.assignment.backend.service;

import static com.assignment.backend.util.UserUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.assignment.backend.dto.RestResponse;
import com.assignment.backend.model.TransactionHistory;
import com.assignment.backend.repository.TransactionHistoryRepository;
import com.assignment.backend.repository.UserRepository;
import com.assignment.backend.service.impl.TransactionServiceImpl;
import com.assignment.backend.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TransactionHistoryRepository transactionHistoryRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_show_current_balance() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());

        ResponseEntity<RestResponse> response = transactionService.showCurrentBalance(1);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("Your Current Balance :: 2000.0",
                Objects.requireNonNull(response.getBody()).getSuccessMessage());
    }

    @Test
    public void test_show_current_balance_when_user_id_is_not_valid() {

        ResponseEntity<RestResponse> response = transactionService.showCurrentBalance(0);

        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("bad request : please input correct user id.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    public void test_show_current_balance_when_user_has_insufficient_balance() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUserWithInsufficientBalance());
        ResponseEntity<RestResponse> response = transactionService.showCurrentBalance(1);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("Your current balance is very low. Please add money to avoid penalty charges.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        assertEquals("Your Current Balance :: 499.5",
                Objects.requireNonNull(response.getBody()).getSuccessMessage());
    }

    @Test
    public void test_add_money_to_account() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());

        ResponseEntity<RestResponse> response = transactionService.addMoneyToAccount(1, 1000);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertNull(response.getBody().getErrorMessage());
    }

    @Test
    public void test_add_money_to_account_when_user_has_insufficient_balance() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUserWithInsufficientBalance());

        ResponseEntity<RestResponse> response = transactionService.addMoneyToAccount(1, 500);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("You have very low balance. So we deducted 500 INR. amount as penalty.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
    }

    @Test
    public void test_add_money_to_account_when_user_does_not_exist() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        ResponseEntity<RestResponse> response = transactionService.addMoneyToAccount(1, 50);
        assertEquals(404, Objects.requireNonNull(response.getBody()).getStatusCode());
    }

    @Test
    public void test_add_money_to_account_when_incorrect_user_id_is_passed() {

        ResponseEntity<RestResponse> response = transactionService.addMoneyToAccount(0, 40);
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
    }

    @Test
    public void test_add_money_to_account_entered_amount_is_less_than_equal_to_1() {

        ResponseEntity<RestResponse> response = transactionService.addMoneyToAccount(1, 1);
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("bad request : please enter amount more than 1.",
                Objects.requireNonNull(response.getBody()).getErrorMessage());
        assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void test_withdraw_money_from_account() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());

        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(1, 1000);
        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertNull(response.getBody().getErrorMessage());
    }

    @Test
    public void test_withdraw_money_from_account_for_incorrect_user_id() {

        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(0, 1000);
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void test_withdraw_money_from_account_for_amount_less_than_equal_to_1() {

        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(1, 1);
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void test_withdraw_money_from_account_for_amount_is_less_than_sufficient_balance() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUserWithInsufficientBalance());
        Mockito.when(transactionHistoryRepository.save(Mockito.any())).thenReturn(new TransactionHistory());
        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(1, 10000);

        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertNull(response.getBody().getSuccessMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void test_withdraw_money_from_account_for_amount_is_less_than_withdrawal_amount() {

        var amountToBeWithdrawn = 10000.0;
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());
        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(1, amountToBeWithdrawn);

        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("Your current balance is less than the withdrawal amount "+amountToBeWithdrawn+" INR." +
                        " Withdrawal is possible when current balance is more than withdrawal amount." +
                        " Your Current Balance is :: 2100.0",
                response.getBody().getErrorMessage());
        assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void test_withdraw_money_from_account_for_amount_is_within_sufficient_balance_range() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());
        Mockito.when(transactionHistoryRepository.save(Mockito.any())).thenReturn(new TransactionHistory());
        ResponseEntity<RestResponse> response = transactionService.withdrawMoneyFromAccount(1, 1600);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("Money withdrawn successfully.\n" +
                "Your Current Balance is :: 400.0", response.getBody().getSuccessMessage());
    }

    @Test
    public void test_user_transaction_history() {

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(createDummyUser());
        Mockito.when(transactionHistoryRepository.findByUserId(Mockito.anyInt())).thenReturn(populateTransactionHistories());
        ResponseEntity<RestResponse> response = transactionService.showUserTransactionHistory(1);

        assertEquals(200, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("found " +populateTransactionHistories().size()+" transactions for user id :: 1", response.getBody().getSuccessMessage());
    }

    @Test
    public void test_user_transaction_history_for_invalid_user_id() {

        ResponseEntity<RestResponse> response = transactionService.showUserTransactionHistory(0);

        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("bad request : please input correct user id.",
                response.getBody().getErrorMessage());
        assertNull(response.getBody().getSuccessMessage());
    }

    @Test
    public void test_user_transaction_history_for_no_user_transactions_found() {

        int userId = 10;
        Mockito.when(transactionHistoryRepository.findByUserId(Mockito.anyInt())).thenReturn(null);

        ResponseEntity<RestResponse> response = transactionService.showUserTransactionHistory(userId);

        assertEquals(404, Objects.requireNonNull(response.getBody()).getStatusCode());
        assertEquals("No User found for the id :: " + userId,
                response.getBody().getErrorMessage());
        assertNull(response.getBody().getSuccessMessage());
    }

    public static List<TransactionHistory> populateTransactionHistories() {

        TransactionHistory transactionHistory = new TransactionHistory(1, 1, LocalDate.now(),
                new BigDecimal(1000), Constants.ACTIVITY_MONEY_WITHDRAWN, Constants.STATUS_SUCCESSFUL);

        TransactionHistory transactionHistory1 = new TransactionHistory(2, 1, LocalDate.now(),
                new BigDecimal(2000), Constants.ACTIVITY_MONEY_WITHDRAWN, Constants.STATUS_SUCCESSFUL);

        TransactionHistory transactionHistory2 = new TransactionHistory(3, 1, LocalDate.now(),
                new BigDecimal(3000), Constants.ACTIVITY_MONEY_DEPOSITED, Constants.STATUS_SUCCESSFUL);

        TransactionHistory transactionHistory3 = new TransactionHistory(4, 1, LocalDate.now(),
                new BigDecimal(10000), Constants.ACTIVITY_MONEY_DEPOSITED, Constants.STATUS_UNSUCCESSFUL);

        return List.of(transactionHistory, transactionHistory1, transactionHistory2, transactionHistory3);
    }
}
