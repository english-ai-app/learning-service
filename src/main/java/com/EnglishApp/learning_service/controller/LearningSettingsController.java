package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.common.UserContextHelper;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.ResetLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.SavedLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.UpdateLearningSettingsRequest;
import com.EnglishApp.learning_service.service.LearningSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = {"/api/v1/learning-settings", "/api/learning/settings"},
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class LearningSettingsController {

    private final LearningSettingsService learningSettingsService;

    @GetMapping
    public LearningSettingsResponse getLearningSettings(HttpServletRequest request) {
        Long userId = UserContextHelper.getAuthenticatedUserId(request);
        return learningSettingsService.getLearningSettings(userId);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public SavedLearningSettingsResponse updateLearningSettings(
            HttpServletRequest request,
            @RequestBody UpdateLearningSettingsRequest body
    ) {
        Long userId = UserContextHelper.getAuthenticatedUserId(request);
        return learningSettingsService.updateLearningSettings(userId, body);
    }

    @PostMapping("/reset")
    public ResetLearningSettingsResponse resetLearningSettings(HttpServletRequest request) {
        Long userId = UserContextHelper.getAuthenticatedUserId(request);
        return learningSettingsService.resetLearningSettings(userId);
    }
}
