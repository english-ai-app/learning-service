package com.EnglishApp.learning_service.repo;

import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {
    Optional<UserVocabulary> findByUserIdAndWordId(Long userId, Long wordId);

    List<UserVocabulary> findByUserId(Long userId);

    List<UserVocabulary> findByUserIdAndStatus(Long userId, Byte status);
}
