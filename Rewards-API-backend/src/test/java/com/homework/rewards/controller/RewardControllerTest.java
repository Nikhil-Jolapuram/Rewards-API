package com.homework.rewards.controller;

import com.homework.rewards.dto.RewardResponse;
import com.homework.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void testGetRewards() throws Exception {
        RewardResponse mockResponse = new RewardResponse(12345L, Map.of("January", 150), 150, List.of());
        Mockito.when(rewardService.calculateRewards(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/rewards")
                        .param("customerId", "12345")
                        .param("startDate", "2023-01-01T00:00:00")
                        .param("endDate", "2023-01-31T23:59:59")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(12345))
                .andExpect(jsonPath("$.monthlyPoints.January").value(150))
                .andExpect(jsonPath("$.totalPoints").value(150));
    }

    @Test
    void testGetRewardsByMonth() throws Exception {
        RewardResponse mockResponse = new RewardResponse(12345L, Map.of("January", 150), 150, List.of());
        Mockito.when(rewardService.calculateRewards(Mockito.anyLong(), Mockito.any(), Mockito.any())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/rewards/2023/01")
                        .param("customerId", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(12345))
                .andExpect(jsonPath("$.monthlyPoints.January").value(150))
                .andExpect(jsonPath("$.totalPoints").value(150));
    }
}
