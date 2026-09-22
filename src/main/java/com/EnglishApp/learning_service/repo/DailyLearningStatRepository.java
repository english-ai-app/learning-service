package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.DailyLearningStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyLearningStatRepository extends JpaRepository<DailyLearningStat, Long> {
    Optional<DailyLearningStat> findByUserIdAndStatDate(Long userId, LocalDate statDate);

    List<DailyLearningStat> findByUserIdAndStatDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );
}
