package com.EnglishApp.learning_service;

import com.EnglishApp.learning_service.common.LearningSettingsConstants;
import com.EnglishApp.learning_service.domain.dto.LearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.ResetLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.SavedLearningSettingsResponse;
import com.EnglishApp.learning_service.domain.dto.UpdateLearningSettingsRequest;
import com.EnglishApp.learning_service.domain.model.OptionSet;
import com.EnglishApp.learning_service.domain.model.OptionSetValue;
import com.EnglishApp.learning_service.domain.model.UserSettingValue;
import com.EnglishApp.learning_service.repo.OptionSetValueRepository;
import com.EnglishApp.learning_service.repo.UserSettingValueRepository;
import com.EnglishApp.learning_service.service.impl.LearningSettingsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningSettingsServiceTest {

    @Mock
    private OptionSetValueRepository optionSetValueRepository;

    @Mock
    private UserSettingValueRepository userSettingValueRepository;

    @InjectMocks
    private LearningSettingsServiceImpl learningSettingsService;

    private OptionSet optionSet;
    private OptionSetValue dailyNewOpt;
    private OptionSetValue dailyReviewOpt;
    private OptionSetValue maxDailyNewOpt;
    private OptionSetValue maxDailyReviewOpt;

    @BeforeEach
    void setUp() {
        optionSet = OptionSet.builder()
                .id(1L)
                .code(LearningSettingsConstants.OPTION_SET_LEARNING_PREFERENCES)
                .name("Learning Preferences")
                .status((byte) 1)
                .build();

        dailyNewOpt = OptionSetValue.builder()
                .id(101L)
                .optionSet(optionSet)
                .code(LearningSettingsConstants.KEY_DAILY_NEW_LIMIT)
                .value("10")
                .dataType("INT")
                .status((byte) 1)
                .build();

        dailyReviewOpt = OptionSetValue.builder()
                .id(102L)
                .optionSet(optionSet)
                .code(LearningSettingsConstants.KEY_DAILY_REVIEW_LIMIT)
                .value("30")
                .dataType("INT")
                .status((byte) 1)
                .build();

        maxDailyNewOpt = OptionSetValue.builder()
                .id(103L)
                .optionSet(optionSet)
                .code(LearningSettingsConstants.KEY_MAX_DAILY_NEW_LIMIT)
                .value("100")
                .dataType("INT")
                .status((byte) 1)
                .build();

        maxDailyReviewOpt = OptionSetValue.builder()
                .id(104L)
                .optionSet(optionSet)
                .code(LearningSettingsConstants.KEY_MAX_DAILY_REVIEW_LIMIT)
                .value("200")
                .dataType("INT")
                .status((byte) 1)
                .build();
    }

    private void mockSystemOptions() {
        when(optionSetValueRepository.findByOptionSet_Code(LearningSettingsConstants.OPTION_SET_LEARNING_PREFERENCES))
                .thenReturn(List.of(dailyNewOpt, dailyReviewOpt, maxDailyNewOpt, maxDailyReviewOpt));
    }

    @Test
    @DisplayName("GET: Người dùng chưa có setting cá nhân -> trả về giá trị mặc định, không tạo bản ghi mới")
    void getLearningSettings_WhenNoUserSettings_ShouldReturnDefaults() {
        Long userId = 1L;
        mockSystemOptions();
        when(userSettingValueRepository.findByUserIdAndOptionSetValue_IdIn(eq(userId), any()))
                .thenReturn(List.of());

        LearningSettingsResponse response = learningSettingsService.getLearningSettings(userId);

        assertNotNull(response);
        assertEquals(10, response.getDailyNewLimit());
        assertEquals(30, response.getDailyReviewLimit());
        assertEquals(40, response.getTotalDailyLimit());

        assertEquals(10, response.getDefaults().getDailyNewLimit());
        assertEquals(30, response.getDefaults().getDailyReviewLimit());
        assertEquals(40, response.getDefaults().getTotalDailyLimit());

        assertEquals(1, response.getConstraints().getMinDailyNewLimit());
        assertEquals(100, response.getConstraints().getMaxDailyNewLimit());
        assertEquals(1, response.getConstraints().getMinDailyReviewLimit());
        assertEquals(200, response.getConstraints().getMaxDailyReviewLimit());

        // Đảm bảo không tạo bản ghi mới khi đọc
        verify(userSettingValueRepository, never()).save(any());
        verify(userSettingValueRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("GET: Người dùng đã có setting cá nhân -> ưu tiên giá trị cá nhân")
    void getLearningSettings_WhenUserSettingsExist_ShouldReturnCustomValues() {
        Long userId = 2L;
        mockSystemOptions();

        UserSettingValue customNew = UserSettingValue.builder()
                .id(1L)
                .userId(userId)
                .optionSetValue(dailyNewOpt)
                .settingValue("25")
                .build();

        UserSettingValue customReview = UserSettingValue.builder()
                .id(2L)
                .userId(userId)
                .optionSetValue(dailyReviewOpt)
                .settingValue("60")
                .build();

        when(userSettingValueRepository.findByUserIdAndOptionSetValue_IdIn(eq(userId), any()))
                .thenReturn(List.of(customNew, customReview));

        LearningSettingsResponse response = learningSettingsService.getLearningSettings(userId);

        assertNotNull(response);
        assertEquals(25, response.getDailyNewLimit());
        assertEquals(60, response.getDailyReviewLimit());
        assertEquals(85, response.getTotalDailyLimit());
        assertEquals(10, response.getDefaults().getDailyNewLimit());
        assertEquals(30, response.getDefaults().getDailyReviewLimit());
    }

    @Test
    @DisplayName("PUT: Lưu cấu hình hợp lệ lần đầu (insert mới)")
    void updateLearningSettings_WhenValidFirstTime_ShouldInsertSettings() {
        Long userId = 3L;
        mockSystemOptions();

        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyNewOpt.getId()))
                .thenReturn(Optional.empty());
        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyReviewOpt.getId()))
                .thenReturn(Optional.empty());

        UpdateLearningSettingsRequest request = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(20)
                .dailyReviewLimit(50)
                .build();

        SavedLearningSettingsResponse response = learningSettingsService.updateLearningSettings(userId, request);

        assertNotNull(response);
        assertEquals(20, response.getDailyNewLimit());
        assertEquals(50, response.getDailyReviewLimit());
        assertEquals(70, response.getTotalDailyLimit());

        verify(userSettingValueRepository, times(2)).saveAndFlush(any(UserSettingValue.class));
    }

    @Test
    @DisplayName("PUT: Cập nhật cấu hình cũ (update bản ghi cũ)")
    void updateLearningSettings_WhenUpdatingExisting_ShouldUpdateValues() {
        Long userId = 4L;
        mockSystemOptions();

        UserSettingValue existingNew = UserSettingValue.builder()
                .id(10L)
                .userId(userId)
                .optionSetValue(dailyNewOpt)
                .settingValue("15")
                .build();

        UserSettingValue existingReview = UserSettingValue.builder()
                .id(11L)
                .userId(userId)
                .optionSetValue(dailyReviewOpt)
                .settingValue("45")
                .build();

        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyNewOpt.getId()))
                .thenReturn(Optional.of(existingNew));
        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyReviewOpt.getId()))
                .thenReturn(Optional.of(existingReview));

        UpdateLearningSettingsRequest request = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(35)
                .dailyReviewLimit(80)
                .build();

        SavedLearningSettingsResponse response = learningSettingsService.updateLearningSettings(userId, request);

        assertEquals(35, response.getDailyNewLimit());
        assertEquals(80, response.getDailyReviewLimit());
        assertEquals(115, response.getTotalDailyLimit());

        assertEquals("35", existingNew.getSettingValue());
        assertEquals("80", existingReview.getSettingValue());
        verify(userSettingValueRepository, times(2)).saveAndFlush(any(UserSettingValue.class));
    }

    @Test
    @DisplayName("PUT: Validation lỗi - Null request hoặc null trường")
    void updateLearningSettings_WhenFieldsNull_ShouldThrowBadRequest() {
        Long userId = 5L;

        assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, null));

        UpdateLearningSettingsRequest reqNullNew = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(null)
                .dailyReviewLimit(50)
                .build();
        assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqNullNew));

        UpdateLearningSettingsRequest reqNullReview = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(20)
                .dailyReviewLimit(null)
                .build();
        assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqNullReview));
    }

    @Test
    @DisplayName("PUT: Validation lỗi - Số âm, bằng 0, hoặc vượt quá giới hạn tối đa hệ thống")
    void updateLearningSettings_WhenValuesOutOfRange_ShouldThrowBadRequest() {
        Long userId = 6L;
        mockSystemOptions();

        // dailyNewLimit = 0 (< 1)
        UpdateLearningSettingsRequest reqZeroNew = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(0)
                .dailyReviewLimit(50)
                .build();
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqZeroNew));
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());

        // dailyNewLimit = 101 (> max 100)
        UpdateLearningSettingsRequest reqOverNew = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(101)
                .dailyReviewLimit(50)
                .build();
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqOverNew));
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());

        // dailyReviewLimit = -5 (< 1)
        UpdateLearningSettingsRequest reqNegativeReview = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(20)
                .dailyReviewLimit(-5)
                .build();
        ResponseStatusException ex3 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqNegativeReview));
        assertEquals(HttpStatus.BAD_REQUEST, ex3.getStatusCode());

        // dailyReviewLimit = 201 (> max 200)
        UpdateLearningSettingsRequest reqOverReview = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(20)
                .dailyReviewLimit(201)
                .build();
        ResponseStatusException ex4 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.updateLearningSettings(userId, reqOverReview));
        assertEquals(HttpStatus.BAD_REQUEST, ex4.getStatusCode());
    }

    @Test
    @DisplayName("POST /reset: Khôi phục mặc định và chỉ xóa đúng 2 cấu hình của nhóm LEARNING_PREFERENCES")
    void resetLearningSettings_ShouldDeleteOnlyTargetSettingsAndReturnDefaults() {
        Long userId = 7L;
        mockSystemOptions();

        ResetLearningSettingsResponse response = learningSettingsService.resetLearningSettings(userId);

        assertNotNull(response);
        assertEquals(10, response.getDailyNewLimit());
        assertEquals(30, response.getDailyReviewLimit());
        assertEquals(40, response.getTotalDailyLimit());
        assertEquals(10, response.getDefaults().getDailyNewLimit());
        assertEquals(30, response.getDefaults().getDailyReviewLimit());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(userSettingValueRepository).deleteByUserIdAndOptionSetValueIdIn(eq(userId), captor.capture());

        Collection<Long> deletedOptionIds = captor.getValue();
        assertEquals(2, deletedOptionIds.size());
        assertTrue(deletedOptionIds.contains(dailyNewOpt.getId()));
        assertTrue(deletedOptionIds.contains(dailyReviewOpt.getId()));
    }

    @Test
    @DisplayName("PUT: Xử lý an toàn tranh chấp đồng thời (DataIntegrityViolationException)")
    void updateLearningSettings_WhenConcurrentInsert_ShouldCatchAndRetryUpdate() {
        Long userId = 8L;
        mockSystemOptions();

        // Lần đầu check chưa thấy
        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyNewOpt.getId()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(UserSettingValue.builder().id(99L).userId(userId).optionSetValue(dailyNewOpt).settingValue("10").build()));

        // Gây lỗi DataIntegrityViolationException khi insert dailyNewOpt
        when(userSettingValueRepository.saveAndFlush(any(UserSettingValue.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key uk_user_setting"))
                .thenReturn(UserSettingValue.builder().build());

        when(userSettingValueRepository.findByUserIdAndOptionSetValueId(userId, dailyReviewOpt.getId()))
                .thenReturn(Optional.empty());

        UpdateLearningSettingsRequest request = UpdateLearningSettingsRequest.builder()
                .dailyNewLimit(30)
                .dailyReviewLimit(40)
                .build();

        SavedLearningSettingsResponse response = learningSettingsService.updateLearningSettings(userId, request);

        assertNotNull(response);
        assertEquals(30, response.getDailyNewLimit());
        assertEquals(40, response.getDailyReviewLimit());
        assertEquals(70, response.getTotalDailyLimit());
    }

    @Test
    @DisplayName("Hệ thống thiếu cấu hình trong option_set_value -> ném lỗi 500")
    void loadSystemOptions_WhenIncomplete_ShouldThrow500() {
        Long userId = 9L;
        // Thiếu MAX_DAILY_REVIEW_LIMIT
        when(optionSetValueRepository.findByOptionSet_Code(LearningSettingsConstants.OPTION_SET_LEARNING_PREFERENCES))
                .thenReturn(List.of(dailyNewOpt, dailyReviewOpt, maxDailyNewOpt));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.getLearningSettings(userId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    @DisplayName("User ID null hoặc <= 0 -> ném 401 Unauthorized")
    void validateUserId_WhenInvalid_ShouldThrow401() {
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.getLearningSettings(null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex1.getStatusCode());

        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> learningSettingsService.getLearningSettings(0L));
        assertEquals(HttpStatus.UNAUTHORIZED, ex2.getStatusCode());
    }
}
