package com.homework.rewards.service;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.entity.Transaction;
import com.homework.rewards.exception.RewardCalculationException;
import com.homework.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Calculate rewards with valid input successfully")
    void calculateRewards_ValidInput_Success() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, customerId, 120.0, LocalDateTime.of(2023, 1, 15, 10, 0)),
                new Transaction(2L, customerId, 80.0, LocalDateTime.of(2023, 2, 20, 14, 30)),
                new Transaction(3L, customerId, 200.0, LocalDateTime.of(2023, 3, 10, 9, 45))
        );

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);

        assertNotNull(response);
        assertEquals(customerId, response.getCustomerId());
        assertEquals(3, response.getMonthlyPoints().size());
        assertEquals(90, response.getMonthlyPoints().get("January"));
        assertEquals(30, response.getMonthlyPoints().get("February"));
        assertEquals(250, response.getMonthlyPoints().get("March"));
        assertEquals(370, response.getTotalPoints());
        assertEquals(transactions, response.getTransactions());
    }

    @Test
    @DisplayName("Throw RewardCalculationException when no transactions are found")
    void calculateRewards_NoTransactions_ThrowsException() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(customerId, startDate, endDate));
    }

    @Test
    @DisplayName("Throw RewardCalculationException when customer ID is null")
    void calculateRewards_NullCustomerId_ThrowsException() {
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(null, startDate, endDate));
    }

    @Test
    @DisplayName("Throw RewardCalculationException when start date is null")
    void calculateRewards_NullStartDate_ThrowsException() {
        Long customerId = 1L;
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(customerId, null, endDate));
    }

    @Test
    @DisplayName("Throw RewardCalculationException when end date is null")
    void calculateRewards_NullEndDate_ThrowsException() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(customerId, startDate, null));
    }

    @Test
    @DisplayName("Throw RewardCalculationException when end date is before start date")
    void calculateRewards_EndDateBeforeStartDate_ThrowsException() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 3, 31, 23, 59);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 1, 0, 0);

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(customerId, startDate, endDate));
    }

    @Test
    @DisplayName("Throw RewardCalculationException when repository throws an exception")
    void calculateRewards_RepositoryThrowsException_ThrowsRewardCalculationException() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 3, 31, 23, 59);

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(RewardCalculationException.class, () ->
                rewardService.calculateRewards(customerId, startDate, endDate));
    }

    @Test
    @DisplayName("Calculate rewards for a transaction amount equal to $100")
    void calculateRewards_TransactionAmount100() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        Transaction transaction = new Transaction(1L, customerId, 100.0, LocalDateTime.of(2023, 1, 10, 10, 0));

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(Collections.singletonList(transaction));

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);

        assertNotNull(response);
        assertEquals(50, response.getMonthlyPoints().get("January")); // 50 points for $100
        assertEquals(50, response.getTotalPoints());
    }

    @Test
    @DisplayName("Calculate rewards for a transaction amount equal to $50")
    void calculateRewards_TransactionAmount50() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        Transaction transaction = new Transaction(1L, customerId, 50.0, LocalDateTime.of(2023, 1, 10, 10, 0));

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(Collections.singletonList(transaction));

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);

        assertNotNull(response);
        assertEquals(0, response.getMonthlyPoints().get("January")); // No points for $50
        assertEquals(0, response.getTotalPoints());
    }

    @Test
    @DisplayName("Calculate rewards for transactions with amounts $50 and $100")
    void calculateRewards_TransactionsAmount50And100() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, customerId, 50.0, LocalDateTime.of(2023, 1, 10, 10, 0)),
                new Transaction(2L, customerId, 100.0, LocalDateTime.of(2023, 1, 15, 12, 0))
        );

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);

        assertNotNull(response);
        assertEquals(50, response.getMonthlyPoints().get("January")); // 50 points for $100, 0 for $50
        assertEquals(50, response.getTotalPoints());
    }

    @Test
    @DisplayName("Calculate rewards for a transaction amount equal to $120")
    void calculateRewards_TransactionAmount120() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        Transaction transaction = new Transaction(1L, customerId, 120.0, LocalDateTime.of(2023, 1, 20, 15, 0));

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(Collections.singletonList(transaction));

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);

        assertNotNull(response);
        assertEquals(90, response.getMonthlyPoints().get("January")); // 90 points for $120
        assertEquals(90, response.getTotalPoints());
    }

    @Test
    @DisplayName("Calculate rewards with transactions on boundary dates")
    void calculateRewards_BoundaryDates_Success() {
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 1, 31, 23, 59);

        List<Transaction> transactions = Collections.singletonList(
                new Transaction(1L, customerId, 50.0, LocalDateTime.of(2023, 1, 1, 0, 0))
        );

        when(transactionRepository.findByCustomerIdAndTimestampBetween(customerId, startDate, endDate))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(customerId, startDate, endDate);
        assertEquals(0, response.getMonthlyPoints().get("January")); // No points for $50 transaction
    }
}
