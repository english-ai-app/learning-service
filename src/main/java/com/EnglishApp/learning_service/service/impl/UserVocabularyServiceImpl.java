package com.EnglishApp.learning_service.service.impl;

import com.EnglishApp.learning_service.common.Const;
import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.model.SrsCard;
import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import com.EnglishApp.learning_service.domain.model.VocabularyCollection;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItem;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItemId;
import com.EnglishApp.learning_service.domain.model.WordMastery;
import com.EnglishApp.learning_service.repo.SrsCardRepository;
import com.EnglishApp.learning_service.repo.UserVocabularyRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionItemRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionRepository;
import com.EnglishApp.learning_service.repo.WordMasteryRepository;
import com.EnglishApp.learning_service.service.UserVocabularyService;
import com.EnglishApp.learning_service.service.UserVocabularyMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserVocabularyServiceImpl implements UserVocabularyService {
    private final UserVocabularyRepository vocabularyRepository;
    private final VocabularyCollectionRepository collectionRepository;
    private final VocabularyCollectionItemRepository itemRepository;
    private final UserVocabularyMediaService mediaService;
    private final SrsCardRepository srsCardRepository;
    private final WordMasteryRepository wordMasteryRepository;

    @Transactional
    @Override
    public Long create(CreateUserVocabularyCommand command) {
        validate(command);

        UserVocabulary vocabulary = vocabularyRepository.save(UserVocabulary.builder()
                .userId(command.getUserId())
                .wordId(command.getWordId())
                .source(command.getSource().trim().toUpperCase(Locale.ROOT))
                .status(Const.USER_VOCABULARY_STATUS.NEW)
                .build());

        mediaService.attachToVocabulary(vocabulary, command.getImageUrl());
        initializeSrsCard(vocabulary);
        initializeWordMastery(vocabulary);

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

    private void initializeSrsCard(UserVocabulary vocabulary) {
        if (srsCardRepository.findByUserVocabularyId(vocabulary.getId()).isPresent()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        srsCardRepository.save(SrsCard.builder()
                .userVocabulary(vocabulary)
                .easeFactor(new BigDecimal("2.50"))
                .intervalDays(0)
                .repetitions(0)
                .successCount(0)
                .lapseCount(0)
                .suspended(false)
                .nextReviewAt(now)
                .lastReviewedAt(null)
                .build());
    }

    private void initializeWordMastery(UserVocabulary vocabulary) {
        if (wordMasteryRepository.findByUserVocabularyId(vocabulary.getId()).isPresent()) {
            return;
        }

        wordMasteryRepository.save(WordMastery.builder()
                .userVocabulary(vocabulary)
                .correctCount(0)
                .wrongCount(0)
                .consecutiveCorrect(0)
                .build());
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
        if (command.getSource() == null || command.getSource().isBlank()) {
            throw new IllegalArgumentException("source is required");
        }
    }
}
