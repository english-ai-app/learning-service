package com.EnglishApp.learning_service.service.impl;

import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import com.EnglishApp.learning_service.domain.model.VocabularyCollection;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItem;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItemId;
import com.EnglishApp.learning_service.repo.UserVocabularyRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionItemRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionRepository;
import com.EnglishApp.learning_service.service.UserVocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserVocabularyServiceImpl implements UserVocabularyService {
    private final UserVocabularyRepository vocabularyRepository;
    private final VocabularyCollectionRepository collectionRepository;
    private final VocabularyCollectionItemRepository itemRepository;

    @Transactional
    @Override
    public Long create(CreateUserVocabularyCommand command) {
        validate(command);

        UserVocabulary vocabulary = vocabularyRepository.save(UserVocabulary.builder()
                .userId(command.getUserId())
                .wordId(command.getWordId())
                .source(command.getSource() == null ? "MANUAL" : command.getSource())
                .status((byte) 1)
                .build());

        if (command.getCollectionId() != null) {
            VocabularyCollection collection = collectionRepository.findById(command.getCollectionId())
                    .filter(item -> command.getUserId().equals(item.getUserId()))
                    .orElseThrow(() -> new IllegalArgumentException("Vocabulary collection not found"));
            itemRepository.save(VocabularyCollectionItem.builder()
                    .id(new VocabularyCollectionItemId(collection.getId(), vocabulary.getId()))
                    .collection(collection)
                    .userVocabulary(vocabulary)
                    .build());
        }
        return vocabulary.getId();
    }

    @Transactional
    @Override
    public void rollback(Long userVocabularyId) {
        if (userVocabularyId == null) {
            return;
        }
        itemRepository.deleteAll(itemRepository.findByUserVocabularyId(userVocabularyId));
        vocabularyRepository.deleteById(userVocabularyId);
    }

    private void validate(CreateUserVocabularyCommand command) {
        if (command == null || command.getUserId() == null || command.getWordId() == null) {
            throw new IllegalArgumentException("userId and wordId are required");
        }
    }
}
