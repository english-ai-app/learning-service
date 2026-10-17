package com.EnglishApp.learning_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSettingsConstraints {
    private Integer minDailyNewLimit;
    private Integer maxDailyNewLimit;
    private Integer minDailyReviewLimit;
    private Integer maxDailyReviewLimit;
    private List<Integer> dailyNewQuickOptions;
    private List<Integer> dailyReviewQuickOptions;
}
