package com.EnglishApp.learning_service;

import com.EnglishApp.learning_service.controller.LearningSettingsController;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsDefaults;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.ResetLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.SavedLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.UpdateLearningSettingsRequest;
import com.EnglishApp.learning_service.service.LearningSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LearningSettingsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LearningSettingsService learningSettingsService;

    @InjectMocks
    private LearningSettingsController learningSettingsController;

    private static final String ACTION_USER_HEADER = "{\"id\":12,\"username\":\"test_user\"}";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(learningSettingsController).build();
    }

    @Test
    @DisplayName("GET: 200 OK khi có header X-Action-User")
    void getLearningSettings_WithActionUserHeader_ShouldReturn200() throws Exception {
        LearningSettingsResponse response = LearningSettingsResponse.builder()
                .dailyNewLimit(10)
                .dailyReviewLimit(30)
                .totalDailyLimit(40)
                .defaults(LearningSettingsDefaults.builder()
                        .dailyNewLimit(10)
                        .dailyReviewLimit(30)
                        .totalDailyLimit(40)
                        .build())
                .build();

        when(learningSettingsService.getLearningSettings(12L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/learning-settings")
                        .header("X-Action-User", ACTION_USER_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyNewLimit").value(10))
                .andExpect(jsonPath("$.dailyReviewLimit").value(30))
                .andExpect(jsonPath("$.totalDailyLimit").value(40));
    }

    @Test
    @DisplayName("GET: 200 OK khi có header X-User-Id")
    void getLearningSettings_WithUserIdHeader_ShouldReturn200() throws Exception {
        LearningSettingsResponse response = LearningSettingsResponse.builder()
                .dailyNewLimit(20)
                .dailyReviewLimit(40)
                .totalDailyLimit(60)
                .build();

        when(learningSettingsService.getLearningSettings(99L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/learning-settings")
                        .header("X-User-Id", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyNewLimit").value(20))
                .andExpect(jsonPath("$.dailyReviewLimit").value(40))
                .andExpect(jsonPath("$.totalDailyLimit").value(60));
    }

    @Test
    @DisplayName("GET: 401 Unauthorized khi không có header xác thực")
    void getLearningSettings_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/learning-settings"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT: 200 OK khi cập nhật thành công")
    void updateLearningSettings_WhenValid_ShouldReturn200() throws Exception {
        SavedLearningSettingsResponse response = SavedLearningSettingsResponse.builder()
                .dailyNewLimit(30)
                .dailyReviewLimit(40)
                .totalDailyLimit(70)
                .build();

        when(learningSettingsService.updateLearningSettings(eq(12L), any(UpdateLearningSettingsRequest.class)))
                .thenReturn(response);

        String jsonBody = "{\"dailyNewLimit\": 30, \"dailyReviewLimit\": 40}";

        mockMvc.perform(put("/api/v1/learning-settings")
                        .header("X-Action-User", ACTION_USER_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyNewLimit").value(30))
                .andExpect(jsonPath("$.dailyReviewLimit").value(40))
                .andExpect(jsonPath("$.totalDailyLimit").value(70));
    }

    @Test
    @DisplayName("PUT: 400 Bad Request khi truyền kiểu không phải số nguyên")
    void updateLearningSettings_WhenNonInteger_ShouldReturn400() throws Exception {
        String invalidJson = "{\"dailyNewLimit\": \"invalid_string\", \"dailyReviewLimit\": 40}";

        mockMvc.perform(put("/api/v1/learning-settings")
                        .header("X-Action-User", ACTION_USER_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /reset: 200 OK khi khôi phục mặc định thành công")
    void resetLearningSettings_WhenSuccess_ShouldReturn200() throws Exception {
        ResetLearningSettingsResponse response = ResetLearningSettingsResponse.builder()
                .dailyNewLimit(10)
                .dailyReviewLimit(30)
                .totalDailyLimit(40)
                .defaults(LearningSettingsDefaults.builder()
                        .dailyNewLimit(10)
                        .dailyReviewLimit(30)
                        .totalDailyLimit(40)
                        .build())
                .build();

        when(learningSettingsService.resetLearningSettings(12L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/learning-settings/reset")
                        .header("X-Action-User", ACTION_USER_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyNewLimit").value(10))
                .andExpect(jsonPath("$.dailyReviewLimit").value(30))
                .andExpect(jsonPath("$.totalDailyLimit").value(40))
                .andExpect(jsonPath("$.defaults.dailyNewLimit").value(10))
                .andExpect(jsonPath("$.defaults.dailyReviewLimit").value(30));
    }
}
