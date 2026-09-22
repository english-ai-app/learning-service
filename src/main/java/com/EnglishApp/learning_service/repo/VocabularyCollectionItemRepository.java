package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItem;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyCollectionItemRepository
        extends JpaRepository<VocabularyCollectionItem, VocabularyCollectionItemId> {
    List<VocabularyCollectionItem> findByCollectionId(Long collectionId);

    List<VocabularyCollectionItem> findByUserVocabularyId(Long userVocabularyId);
}
