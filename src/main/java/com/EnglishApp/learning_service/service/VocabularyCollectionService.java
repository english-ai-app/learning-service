package com.EnglishApp.learning_service.service;

import com.EnglishApp.learning_service.domain.dto.CreateVocabularyCollectionRequest;
import com.EnglishApp.learning_service.domain.dto.VocabularyCollectionDTO;

import java.util.List;

public interface VocabularyCollectionService {
    List<VocabularyCollectionDTO> getCollections(Long userId);

    VocabularyCollectionDTO createCollection(CreateVocabularyCollectionRequest request);
}
