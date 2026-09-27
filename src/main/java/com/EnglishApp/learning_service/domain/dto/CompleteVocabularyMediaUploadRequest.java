package com.EnglishApp.learning_service.domain.dto;

import lombok.Data;

@Data
public class CompleteVocabularyMediaUploadRequest {
    private Long userId;
    private String objectKey;
    private Boolean primary;
}