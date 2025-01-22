package com.homework.rewards.exception;

import com.homework.rewards.controller.RewardController;
import com.homework.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RewardController.class)
public class RewardCalculationExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;


    @Test
    void testGetRewards_InvalidInput() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("customerId", "")
                        .param("startDate", "2023-01-01T00:00:00")
                        .param("endDate", "2023-01-31T23:59:59")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
