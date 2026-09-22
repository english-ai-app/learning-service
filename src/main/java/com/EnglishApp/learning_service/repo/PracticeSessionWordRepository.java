package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.PracticeSessionWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PracticeSessionWordRepository extends JpaRepository<PracticeSessionWord, Long> {
    List<PracticeSessionWord> findBySessionId(Long sessionId);

    Optional<PracticeSessionWord> findBySessionIdAndUserVocabularyId(Long sessionId, Long userVocabularyId);
}
