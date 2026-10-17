package com.EnglishApp.learning_service.service;

import com.EnglishApp.learning_service.domain.dto.LearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.ResetLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.SavedLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.UpdateLearningSettingsRequest;

public interface LearningSettingsService {

    LearningSettingsResponse getLearningSettings(Long userId);

    SavedLearningSettingsResponse updateLearningSettings(Long userId, UpdateLearningSettingsRequest request);

    ResetLearningSettingsResponse resetLearningSettings(Long userId);
}
