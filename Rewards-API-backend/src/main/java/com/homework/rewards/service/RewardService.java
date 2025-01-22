package com.homework.rewards.service;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.entity.Transaction;
import com.homework.rewards.exception.RewardCalculationException;
import com.homework.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final TransactionRepository transactionRepository;

    public RewardResponse calculateRewards(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            // Validate inputs
            if (customerId == null || startDate == null || endDate == null) {
                throw new IllegalArgumentException("Customer ID, start date, and end date must not be null.");
            }
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date cannot be before start date.");
            }

            // Fetch transactions
            List<Transaction> transactions = transactionRepository.findByCustomerIdAndTimestampBetween(
                    customerId, startDate, endDate
            );

            // Check if transactions exist
            if (transactions == null || transactions.isEmpty()) {
                throw new NoSuchElementException("No transactions found for the given customer and date range.");
            }

            // Calculate rewards
            Map<String, Integer> monthlyPoints = transactions.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getTimestamp().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            Collectors.summingInt(this::calculatePoints)
                    ));

            int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

            // Return response with transactions included
            return new RewardResponse(customerId, monthlyPoints, totalPoints, transactions);

        } catch (IllegalArgumentException e) {
            throw new RewardCalculationException("Invalid input: " + e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new RewardCalculationException("Data error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RewardCalculationException("An unexpected error occurred while calculating rewards.", e);
        }
    }

    private int calculatePoints(Transaction transaction) {
        try {
            double amount = transaction.getAmount();
            int points = 0;
            if (amount > 100) {
                points += (amount - 100) * 2;
                amount = 100;
            }
            if (amount > 50) {
                points += (amount - 50);
            }
            return points;
        } catch (Exception e) {
            throw new RewardCalculationException("Error while calculating points for a transaction.", e);
        }
    }
}