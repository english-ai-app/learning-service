package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.PracticeAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeAnswerRepository extends JpaRepository<PracticeAnswer, Long> {
    List<PracticeAnswer> findBySessionId(Long sessionId);

    List<PracticeAnswer> findByQuestionId(Long questionId);
}
