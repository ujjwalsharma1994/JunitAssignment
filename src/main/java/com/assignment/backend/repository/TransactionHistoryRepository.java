package com.assignment.backend.repository;

import com.assignment.backend.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {

    List<TransactionHistory> findByUserId(int userId);
}