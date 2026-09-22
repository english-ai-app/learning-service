package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.UserLessonStepProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLessonStepProgressRepository extends JpaRepository<UserLessonStepProgress, Long> {
    Optional<UserLessonStepProgress> findByUserIdAndLessonStepId(Long userId, Long lessonStepId);

    List<UserLessonStepProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
}
