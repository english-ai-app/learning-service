package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
    List<ReviewLog> findBySrsCardId(Long srsCardId);

    List<ReviewLog> findByUserIdAndReviewedAtBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
