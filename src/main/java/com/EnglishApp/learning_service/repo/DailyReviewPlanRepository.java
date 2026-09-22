package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.DailyReviewPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyReviewPlanRepository extends JpaRepository<DailyReviewPlan, Long> {
    Optional<DailyReviewPlan> findByUserIdAndPlanDate(Long userId, LocalDate planDate);

    List<DailyReviewPlan> findByUserIdAndStatus(Long userId, Byte status);
}
