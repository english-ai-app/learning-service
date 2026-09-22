package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.VocabularyCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyCollectionRepository extends JpaRepository<VocabularyCollection, Long> {
    List<VocabularyCollection> findByUserId(Long userId);

    List<VocabularyCollection> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<VocabularyCollection> findByUserIdAndStatus(Long userId, Byte status);
}
