package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.UserVocabularyMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserVocabularyMediaRepository extends JpaRepository<UserVocabularyMedia, Long> {
    List<UserVocabularyMedia> findByUserVocabularyId(Long userVocabularyId);

    List<UserVocabularyMedia> findByUserVocabularyIdAndPrimary(Long userVocabularyId, Boolean primary);
}
