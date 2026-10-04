package com.EnglishApp.learning_service.service;

import com.EnglishApp.learning_service.domain.dto.CompleteVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.dto.PrepareVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.model.UserVocabularyMedia;
import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import com.EnglishApp.learning_service.storage.PresignedUpload;
import org.springframework.web.multipart.MultipartFile;

public interface UserVocabularyMediaService {
    String uploadPending(Long userId, MultipartFile file);

    void attachToVocabulary(UserVocabulary vocabulary, String imageUrl);

    UserVocabularyMedia upload(Long vocabularyId, Long userId, MultipartFile file);

    PresignedUpload prepareUpload(Long vocabularyId, PrepareVocabularyMediaUploadRequest request);

    UserVocabularyMedia completeUpload(Long vocabularyId, CompleteVocabularyMediaUploadRequest request);
}
