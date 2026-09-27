package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.domain.dto.CompleteVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.dto.PrepareVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.model.UserVocabularyMedia;
import com.EnglishApp.learning_service.service.UserVocabularyMediaService;
import com.EnglishApp.learning_service.storage.PresignedUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning/user-vocabularies/{vocabularyId}/media")
public class UserVocabularyMediaController {
    private final UserVocabularyMediaService mediaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserVocabularyMedia upload(@PathVariable Long vocabularyId,
                                      @RequestParam Long userId,
                                      @RequestPart("file") MultipartFile file) {
        return mediaService.upload(vocabularyId, userId, file);
    }

    @PostMapping(value = "/upload-url", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PresignedUpload prepareUpload(@PathVariable Long vocabularyId,
                                         @RequestBody PrepareVocabularyMediaUploadRequest request) {
        return mediaService.prepareUpload(vocabularyId, request);
    }

    @PostMapping(value = "/complete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserVocabularyMedia completeUpload(@PathVariable Long vocabularyId,
                                              @RequestBody CompleteVocabularyMediaUploadRequest request) {
        return mediaService.completeUpload(vocabularyId, request);
    }
}