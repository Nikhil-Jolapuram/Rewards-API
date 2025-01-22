package com.homework.rewards.controller;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @GetMapping
    public RewardResponse getRewards(@RequestParam Long customerId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return rewardService.calculateRewards(customerId, startDate, endDate);
    }

    @GetMapping("/{year}/{month}")
    public RewardResponse getRewardsByMonth(
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam Long customerId) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);
        return rewardService.calculateRewards(customerId, startDate, endDate);
    }
}