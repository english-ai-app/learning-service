package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.WordMastery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WordMasteryRepository extends JpaRepository<WordMastery, Long> {
    Optional<WordMastery> findByUserVocabularyId(Long userVocabularyId);
}
