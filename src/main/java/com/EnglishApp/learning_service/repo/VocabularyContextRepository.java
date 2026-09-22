package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.VocabularyContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyContextRepository extends JpaRepository<VocabularyContext, Long> {
    List<VocabularyContext> findByUserVocabularyId(Long userVocabularyId);

    List<VocabularyContext> findByContextType(String contextType);
}
