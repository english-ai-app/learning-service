package com.EnglishApp.learning_service.service.impl;

import com.EnglishApp.learning_service.common.LearningSettingsConstants;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsConstraints;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsDefaults;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.ResetLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.SavedLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.UpdateLearningSettingsRequest;
import com.EnglishApp.learning_service.domain.model.OptionSetValue;
import com.EnglishApp.learning_service.domain.model.UserSettingValue;
import com.EnglishApp.learning_service.repo.OptionSetValueRepository;
import com.EnglishApp.learning_service.repo.UserSettingValueRepository;
import com.EnglishApp.learning_service.service.LearningSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningSettingsServiceImpl implements LearningSettingsService {

    private final OptionSetValueRepository optionSetValueRepository;
    private final UserSettingValueRepository userSettingValueRepository;

    @Override
    @Transactional(readOnly = true)
    public LearningSettingsResponse getLearningSettings(Long userId) {
        validateUserId(userId);
        SystemLearningOptions systemOptions = loadSystemOptions();

        int defaultDailyNew = systemOptions.getDefaultDailyNewLimit();
        int defaultDailyReview = systemOptions.getDefaultDailyReviewLimit();
        int maxDailyNew = systemOptions.getMaxDailyNewLimit();
        int maxDailyReview = systemOptions.getMaxDailyReviewLimit();

        List<UserSettingValue> userSettings = userSettingValueRepository.findByUserIdAndOptionSetValue_IdIn(
                userId,
                List.of(systemOptions.getDailyNewOption().getId(), systemOptions.getDailyReviewOption().getId())
        );

        Map<Long, String> userSettingMap = userSettings.stream()
                .collect(Collectors.toMap(
                        setting -> setting.getOptionSetValue().getId(),
                        UserSettingValue::getSettingValue,
                        (existing, replacement) -> replacement
                ));

        int effectiveDailyNew = parseUserSettingOrDefault(
                userSettingMap.get(systemOptions.getDailyNewOption().getId()),
                defaultDailyNew,
                LearningSettingsConstants.DEFAULT_MIN_LIMIT,
                maxDailyNew
        );

        int effectiveDailyReview = parseUserSettingOrDefault(
                userSettingMap.get(systemOptions.getDailyReviewOption().getId()),
                defaultDailyReview,
                LearningSettingsConstants.DEFAULT_MIN_LIMIT,
                maxDailyReview
        );

        int totalDailyLimit = effectiveDailyNew + effectiveDailyReview;
        int defaultTotalDailyLimit = defaultDailyNew + defaultDailyReview;

        LearningSettingsDefaults defaults = LearningSettingsDefaults.builder()
                .dailyNewLimit(defaultDailyNew)
                .dailyReviewLimit(defaultDailyReview)
                .totalDailyLimit(defaultTotalDailyLimit)
                .build();

        LearningSettingsConstraints constraints = LearningSettingsConstraints.builder()
                .minDailyNewLimit(LearningSettingsConstants.DEFAULT_MIN_LIMIT)
                .maxDailyNewLimit(maxDailyNew)
                .minDailyReviewLimit(LearningSettingsConstants.DEFAULT_MIN_LIMIT)
                .maxDailyReviewLimit(maxDailyReview)
                .dailyNewQuickOptions(filterQuickOptions(LearningSettingsConstants.DEFAULT_QUICK_OPTIONS_DAILY_NEW, maxDailyNew))
                .dailyReviewQuickOptions(filterQuickOptions(LearningSettingsConstants.DEFAULT_QUICK_OPTIONS_DAILY_REVIEW, maxDailyReview))
                .build();

        return LearningSettingsResponse.builder()
                .dailyNewLimit(effectiveDailyNew)
                .dailyReviewLimit(effectiveDailyReview)
                .totalDailyLimit(totalDailyLimit)
                .defaults(defaults)
                .constraints(constraints)
                .build();
    }

    @Override
    @Transactional
    public SavedLearningSettingsResponse updateLearningSettings(Long userId, UpdateLearningSettingsRequest request) {
        validateUserId(userId);
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body cannot be null");
        }
        if (request.getDailyNewLimit() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dailyNewLimit is required and must be an integer");
        }
        if (request.getDailyReviewLimit() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dailyReviewLimit is required and must be an integer");
        }

        SystemLearningOptions systemOptions = loadSystemOptions();
        int maxDailyNew = systemOptions.getMaxDailyNewLimit();
        int maxDailyReview = systemOptions.getMaxDailyReviewLimit();

        if (request.getDailyNewLimit() < LearningSettingsConstants.DEFAULT_MIN_LIMIT || request.getDailyNewLimit() > maxDailyNew) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("dailyNewLimit must be between %d and %d", LearningSettingsConstants.DEFAULT_MIN_LIMIT, maxDailyNew)
            );
        }

        if (request.getDailyReviewLimit() < LearningSettingsConstants.DEFAULT_MIN_LIMIT || request.getDailyReviewLimit() > maxDailyReview) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("dailyReviewLimit must be between %d and %d", LearningSettingsConstants.DEFAULT_MIN_LIMIT, maxDailyReview)
            );
        }

        upsertUserSetting(userId, systemOptions.getDailyNewOption(), String.valueOf(request.getDailyNewLimit()));
        upsertUserSetting(userId, systemOptions.getDailyReviewOption(), String.valueOf(request.getDailyReviewLimit()));

        int totalDailyLimit = request.getDailyNewLimit() + request.getDailyReviewLimit();

        return SavedLearningSettingsResponse.builder()
                .dailyNewLimit(request.getDailyNewLimit())
                .dailyReviewLimit(request.getDailyReviewLimit())
                .totalDailyLimit(totalDailyLimit)
                .build();
    }

    @Override
    @Transactional
    public ResetLearningSettingsResponse resetLearningSettings(Long userId) {
        validateUserId(userId);
        SystemLearningOptions systemOptions = loadSystemOptions();

        List<Long> optionIds = List.of(
                systemOptions.getDailyNewOption().getId(),
                systemOptions.getDailyReviewOption().getId()
        );

        userSettingValueRepository.deleteByUserIdAndOptionSetValueIdIn(userId, optionIds);

        int defaultDailyNew = systemOptions.getDefaultDailyNewLimit();
        int defaultDailyReview = systemOptions.getDefaultDailyReviewLimit();
        int defaultTotal = defaultDailyNew + defaultDailyReview;

        LearningSettingsDefaults defaults = LearningSettingsDefaults.builder()
                .dailyNewLimit(defaultDailyNew)
                .dailyReviewLimit(defaultDailyReview)
                .totalDailyLimit(defaultTotal)
                .build();

        return ResetLearningSettingsResponse.builder()
                .dailyNewLimit(defaultDailyNew)
                .dailyReviewLimit(defaultDailyReview)
                .totalDailyLimit(defaultTotal)
                .defaults(defaults)
                .build();
    }

    private void upsertUserSetting(Long userId, OptionSetValue optionSetValue, String settingValue) {
        try {
            Optional<UserSettingValue> existingOpt = userSettingValueRepository
                    .findByUserIdAndOptionSetValueId(userId, optionSetValue.getId());

            if (existingOpt.isPresent()) {
                UserSettingValue existing = existingOpt.get();
                existing.setSettingValue(settingValue);
                userSettingValueRepository.saveAndFlush(existing);
            } else {
                UserSettingValue newValue = UserSettingValue.builder()
                        .userId(userId)
                        .optionSetValue(optionSetValue)
                        .settingValue(settingValue)
                        .build();
                userSettingValueRepository.saveAndFlush(newValue);
            }
        } catch (DataIntegrityViolationException ex) {
            log.warn("Concurrent insert detected for user {} optionSetValue {}. Retrying update...",
                    userId, optionSetValue.getId());
            UserSettingValue reloaded = userSettingValueRepository
                    .findByUserIdAndOptionSetValueId(userId, optionSetValue.getId())
                    .orElseThrow(() -> ex);
            reloaded.setSettingValue(settingValue);
            userSettingValueRepository.saveAndFlush(reloaded);
        }
    }

    private SystemLearningOptions loadSystemOptions() {
        List<OptionSetValue> optionSetValues = optionSetValueRepository
                .findByOptionSet_Code(LearningSettingsConstants.OPTION_SET_LEARNING_PREFERENCES);

        Map<String, OptionSetValue> optionMap = optionSetValues.stream()
                .collect(Collectors.toMap(
                        OptionSetValue::getCode,
                        option -> option,
                        (existing, replacement) -> existing
                ));

        OptionSetValue dailyNewOpt = optionMap.get(LearningSettingsConstants.KEY_DAILY_NEW_LIMIT);
        OptionSetValue dailyReviewOpt = optionMap.get(LearningSettingsConstants.KEY_DAILY_REVIEW_LIMIT);
        OptionSetValue maxDailyNewOpt = optionMap.get(LearningSettingsConstants.KEY_MAX_DAILY_NEW_LIMIT);
        OptionSetValue maxDailyReviewOpt = optionMap.get(LearningSettingsConstants.KEY_MAX_DAILY_REVIEW_LIMIT);

        if (dailyNewOpt == null || dailyReviewOpt == null || maxDailyNewOpt == null || maxDailyReviewOpt == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Learning preferences system configuration is incomplete in option_set_value"
            );
        }

        int defaultDailyNew = parseInteger(dailyNewOpt.getValue(), LearningSettingsConstants.KEY_DAILY_NEW_LIMIT);
        int defaultDailyReview = parseInteger(dailyReviewOpt.getValue(), LearningSettingsConstants.KEY_DAILY_REVIEW_LIMIT);
        int maxDailyNew = parseInteger(maxDailyNewOpt.getValue(), LearningSettingsConstants.KEY_MAX_DAILY_NEW_LIMIT);
        int maxDailyReview = parseInteger(maxDailyReviewOpt.getValue(), LearningSettingsConstants.KEY_MAX_DAILY_REVIEW_LIMIT);

        return new SystemLearningOptions(
                dailyNewOpt,
                dailyReviewOpt,
                maxDailyNewOpt,
                maxDailyReviewOpt,
                defaultDailyNew,
                defaultDailyReview,
                maxDailyNew,
                maxDailyReview
        );
    }

    private int parseInteger(String value, String keyName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Invalid integer configuration for %s: %s", keyName, value)
            );
        }
    }

    private int parseUserSettingOrDefault(String settingValue, int defaultValue, int min, int max) {
        if (settingValue == null || settingValue.isBlank()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(settingValue.trim());
            if (parsed >= min && parsed <= max) {
                return parsed;
            }
            return defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private List<Integer> filterQuickOptions(List<Integer> options, int maxLimit) {
        return options.stream()
                .filter(opt -> opt <= maxLimit)
                .toList();
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
    }

    private record SystemLearningOptions(
            OptionSetValue dailyNewOption,
            OptionSetValue dailyReviewOption,
            OptionSetValue maxDailyNewOption,
            OptionSetValue maxDailyReviewOption,
            int defaultDailyNewLimit,
            int defaultDailyReviewLimit,
            int maxDailyNewLimit,
            int maxDailyReviewLimit
    ) {
        public OptionSetValue getDailyNewOption() {
            return dailyNewOption;
        }

        public OptionSetValue getDailyReviewOption() {
            return dailyReviewOption;
        }

        public int getDefaultDailyNewLimit() {
            return defaultDailyNewLimit;
        }

        public int getDefaultDailyReviewLimit() {
            return defaultDailyReviewLimit;
        }

        public int getMaxDailyNewLimit() {
            return maxDailyNewLimit;
        }

        public int getMaxDailyReviewLimit() {
            return maxDailyReviewLimit;
        }
    }
}
