package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.LessonAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonAttemptRepository extends JpaRepository<LessonAttempt, Long> {
    List<LessonAttempt> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<LessonAttempt> findByPracticeSessionId(Long practiceSessionId);
}
