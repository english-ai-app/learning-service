package com.EnglishApp.learning_service.domain.dto;

import lombok.Data;

@Data
public class PrepareVocabularyMediaUploadRequest {
    private Long userId;
    private String contentType;
    private Long contentLength;
}