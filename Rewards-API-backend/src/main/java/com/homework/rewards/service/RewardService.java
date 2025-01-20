package com.homework.rewards.service;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.entity.Transaction;
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
        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate);

        Map<String, Integer> monthlyPoints = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.summingInt(this::calculatePoints)));

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
        return new RewardResponse(customerId, monthlyPoints, totalPoints);
    }

    private int calculatePoints(Transaction transaction) {
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
    }
}