package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.UserCourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long> {
    Optional<UserCourseProgress> findByUserIdAndCourseId(Long userId, Long courseId);

    List<UserCourseProgress> findByUserIdAndStatus(Long userId, String status);
}
