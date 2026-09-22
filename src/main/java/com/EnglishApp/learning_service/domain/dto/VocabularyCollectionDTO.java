package com.EnglishApp.learning_service.domain.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class VocabularyCollectionDTO {
    Long id;
    Long userId;
    String name;
    String description;
    String iconUrl;
    Byte status;
    Integer maxWords;
    LocalDateTime startedAt;
    LocalDateTime completedAt;
    LocalDateTime lastPracticedAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
