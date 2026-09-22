package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.DailyReviewPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DailyReviewPlanItemRepository extends JpaRepository<DailyReviewPlanItem, Long> {
    List<DailyReviewPlanItem> findByPlanId(Long planId);

    List<DailyReviewPlanItem> findByPlanIdAndStatus(Long planId, Byte status);

    Optional<DailyReviewPlanItem> findByPlanIdAndUserVocabularyId(Long planId, Long userVocabularyId);
}
