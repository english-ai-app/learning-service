package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.PracticeSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {
    List<PracticeSession> findByUserId(Long userId);

    List<PracticeSession> findByUserIdAndStatus(Long userId, Byte status);
}
