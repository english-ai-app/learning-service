package com.EnglishApp.learning_service.common;

import java.util.List;

public final class LearningSettingsConstants {
    private LearningSettingsConstants() {
    }

    public static final String OPTION_SET_LEARNING_PREFERENCES = "LEARNING_PREFERENCES";

    public static final String KEY_DAILY_NEW_LIMIT = "DAILY_NEW_LIMIT";
    public static final String KEY_DAILY_REVIEW_LIMIT = "DAILY_REVIEW_LIMIT";
    public static final String KEY_MAX_DAILY_NEW_LIMIT = "MAX_DAILY_NEW_LIMIT";
    public static final String KEY_MAX_DAILY_REVIEW_LIMIT = "MAX_DAILY_REVIEW_LIMIT";

    public static final int DEFAULT_MIN_LIMIT = 1;

    public static final List<Integer> DEFAULT_QUICK_OPTIONS_DAILY_NEW = List.of(5, 10, 15, 20, 30);
    public static final List<Integer> DEFAULT_QUICK_OPTIONS_DAILY_REVIEW = List.of(15, 30, 50, 100);
}
