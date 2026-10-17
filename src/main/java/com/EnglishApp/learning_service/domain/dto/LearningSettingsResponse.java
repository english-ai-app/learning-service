package com.EnglishApp.learning_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSettingsResponse {
    private Integer dailyNewLimit;
    private Integer dailyReviewLimit;
    private Integer totalDailyLimit;
    private LearningSettingsDefaults defaults;
    private LearningSettingsConstraints constraints;
}
