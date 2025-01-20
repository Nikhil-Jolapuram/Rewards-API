package com.homework.rewards.repository;

import com.homework.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdAndTimestampBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}