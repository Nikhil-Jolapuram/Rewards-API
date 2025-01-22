package com.homework.rewards.dto;

import com.homework.rewards.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RewardResponse {
    private Long customerId;
    private Map<String, Integer> monthlyPoints; // Month-wise rewards
    private int totalPoints; // Total points
    private List<Transaction> transactions; // Transaction details
}