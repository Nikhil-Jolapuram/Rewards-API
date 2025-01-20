package com.homework.rewards.service;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.entity.Transaction;
import com.homework.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RewardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("No transactions in the given date range")
    void testNoTransactionsInDateRange() {
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(List.of());

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(0, response.getTotalPoints(), "Total points should be 0 for no transactions");
        assertEquals(0, response.getRewardsSummary().size(), "Rewards summary should be empty for no transactions");
    }

    @Test
    @DisplayName("Single transaction below $50 earns no points")
    void testSingleTransactionBelow50() {
        Transaction transaction = new Transaction(1L, 1L, 40.0, LocalDateTime.now());
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(List.of(transaction));

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(0, response.getTotalPoints(), "Total points should be 0 for transactions below $50");
    }

    @Test
    @DisplayName("Single transaction between $50 and $100 earns correct points")
    void testSingleTransactionBetween50And100() {
        Transaction transaction = new Transaction(1L, 1L, 75.0, LocalDateTime.now());
        when(transactionRepository.findByCustomerIdAndTimestampBetween(
                1L,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        )).thenReturn(List.of(transaction));

        RewardResponse response = rewardService.calculateRewards(
                1L,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        System.out.println("Monthly Points: " + response.getRewardsSummary());
        System.out.println("Total Points: " + response.getTotalPoints());

        assertEquals(25, response.getTotalPoints(), "Points should be correctly calculated for amounts between $50 and $100");
    }


    @Test
    @DisplayName("Single transaction above $100 earns correct points")
    void testSingleTransactionAbove100() {
        Transaction transaction = new Transaction(1L, 1L, 150.0, LocalDateTime.now());
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(List.of(transaction));

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(150, response.getTotalPoints(), "Points should be correctly calculated for amounts above $100");
    }

    @Test
    @DisplayName("Multiple transactions in the same month are summed correctly")
    void testMultipleTransactionsSameMonth() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, 1L, 60.0, LocalDateTime.now()),
                new Transaction(2L, 1L, 120.0, LocalDateTime.now())
        );
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(140, response.getTotalPoints(), "Points should be correctly summed for multiple transactions in the same month");
    }

    @Test
    @DisplayName("Transactions across multiple months are grouped correctly")
    void testTransactionsAcrossMultipleMonths() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, 1L, 120.0, LocalDateTime.of(2023, 12, 1, 10, 0)),
                new Transaction(2L, 1L, 75.0, LocalDateTime.of(2023, 11, 15, 10, 0))
        );
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.of(2023, 11, 1, 0, 0), LocalDateTime.of(2023, 12, 31, 23, 59)))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.of(2023, 11, 1, 0, 0), LocalDateTime.of(2023, 12, 31, 23, 59));

        assertEquals(115, response.getTotalPoints(), "Points should be correctly calculated across multiple months");
        assertEquals(2, response.getRewardsSummary().size(), "Rewards summary should have entries for each month");
    }

    @Test
    @DisplayName("Transactions with negative or zero amounts earn no points")
    void testNegativeOrZeroAmounts() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, 1L, 0.0, LocalDateTime.now()),
                new Transaction(2L, 1L, -50.0, LocalDateTime.now())
        );
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(0, response.getTotalPoints(), "Transactions with zero or negative amounts should not contribute to points");
    }

    @Test
    @DisplayName("No transactions for invalid customer ID")
    void testInvalidCustomerId() {
        when(transactionRepository.findByCustomerIdAndTimestampBetween(999L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(List.of());

        RewardResponse response = rewardService.calculateRewards(999L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(0, response.getTotalPoints(), "Total points should be 0 for invalid customer ID");
    }

    @Test
    @DisplayName("Large number of transactions are handled correctly")
    void testLargeNumberOfTransactions() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, 1L, 100.0, LocalDateTime.now()),
                new Transaction(2L, 1L, 200.0, LocalDateTime.now()),
                new Transaction(3L, 1L, 300.0, LocalDateTime.now())
        );
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(transactions);

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(800, response.getTotalPoints(), "Total points should be correctly summed for a large number of transactions");
    }

    @Test
    @DisplayName("Transaction dates outside the range are ignored")
    void testTransactionsOutsideDateRange() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, 1L, 100.0, LocalDateTime.now().minusDays(40)),
                new Transaction(2L, 1L, 150.0, LocalDateTime.now())
        );
        when(transactionRepository.findByCustomerIdAndTimestampBetween(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now()))
                .thenReturn(List.of(transactions.get(1)));

        RewardResponse response = rewardService.calculateRewards(1L, LocalDateTime.now().minusDays(30), LocalDateTime.now());

        assertEquals(200, response.getTotalPoints(), "Points should only include transactions within the given date range");
    }
}
