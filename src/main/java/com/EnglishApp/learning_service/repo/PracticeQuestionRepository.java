package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.PracticeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, Long> {
    List<PracticeQuestion> findBySessionId(Long sessionId);

    List<PracticeQuestion> findByUserVocabularyId(Long userVocabularyId);
}
