package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.SrsCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SrsCardRepository extends JpaRepository<SrsCard, Long> {
    Optional<SrsCard> findByUserVocabularyId(Long userVocabularyId);

    List<SrsCard> findBySuspendedFalseAndNextReviewAtLessThanEqual(LocalDateTime nextReviewAt);
}
