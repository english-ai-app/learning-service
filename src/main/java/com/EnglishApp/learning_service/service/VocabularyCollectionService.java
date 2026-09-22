package com.EnglishApp.learning_service.service;

import com.EnglishApp.learning_service.domain.dto.CreateVocabularyCollectionRequest;
import com.EnglishApp.learning_service.domain.dto.VocabularyCollectionDTO;
import com.EnglishApp.learning_service.domain.model.VocabularyCollection;
import com.EnglishApp.learning_service.repo.VocabularyCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VocabularyCollectionService {
    private static final byte STATUS_NEW = 0;

    private final VocabularyCollectionRepository collectionRepository;

    @Transactional(readOnly = true)
    public List<VocabularyCollectionDTO> getCollections(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        return collectionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public VocabularyCollectionDTO createCollection(CreateVocabularyCollectionRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (isBlank(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        VocabularyCollection collection = VocabularyCollection.builder()
                .userId(request.getUserId())
                .name(request.getName().trim())
                .description(trimToNull(request.getDescription()))
                .iconUrl(trimToNull(request.getIconUrl()))
                .status(STATUS_NEW)
                .maxWords(request.getMaxWords())
                .build();

        return toDTO(collectionRepository.save(collection));
    }

    private VocabularyCollectionDTO toDTO(VocabularyCollection collection) {
        return VocabularyCollectionDTO.builder()
                .id(collection.getId())
                .userId(collection.getUserId())
                .name(collection.getName())
                .description(collection.getDescription())
                .iconUrl(collection.getIconUrl())
                .status(collection.getStatus())
                .maxWords(collection.getMaxWords())
                .startedAt(collection.getStartedAt())
                .completedAt(collection.getCompletedAt())
                .lastPracticedAt(collection.getLastPracticedAt())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
