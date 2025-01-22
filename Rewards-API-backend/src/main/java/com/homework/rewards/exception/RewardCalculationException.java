package com.homework.rewards.exception;

public class RewardCalculationException extends RuntimeException {
    public RewardCalculationException(String message) {
        super(message);
    }

    public RewardCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
