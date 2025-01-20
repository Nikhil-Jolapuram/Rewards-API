package com.homework.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class RewardResponse {
    private Long customerId;
    private Map<String, Integer> rewardsSummary;
    private int totalPoints;
}