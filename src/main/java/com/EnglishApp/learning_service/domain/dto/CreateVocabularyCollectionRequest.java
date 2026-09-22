package com.EnglishApp.learning_service.domain.dto;

import lombok.Data;

@Data
public class CreateVocabularyCollectionRequest {
    private Long userId;
    private String name;
    private String description;
    private String iconUrl;
    private Integer maxWords;
}
