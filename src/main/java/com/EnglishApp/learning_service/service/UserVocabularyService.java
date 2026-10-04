package com.EnglishApp.learning_service.service;

import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;

public interface UserVocabularyService {
    Long create(CreateUserVocabularyCommand command);

    void rollback(Long userVocabularyId);
}
