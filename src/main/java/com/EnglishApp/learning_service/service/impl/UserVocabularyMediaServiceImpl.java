package com.EnglishApp.learning_service.service.impl;

import com.EnglishApp.learning_service.domain.dto.CompleteVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.dto.PrepareVocabularyMediaUploadRequest;
import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import com.EnglishApp.learning_service.domain.model.UserVocabularyMedia;
import com.EnglishApp.learning_service.repo.UserVocabularyMediaRepository;
import com.EnglishApp.learning_service.repo.UserVocabularyRepository;
import com.EnglishApp.learning_service.storage.PresignedUpload;
import com.EnglishApp.learning_service.storage.StorageProperties;
import com.EnglishApp.learning_service.storage.StorageProvider;
import com.EnglishApp.learning_service.service.UserVocabularyMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserVocabularyMediaServiceImpl implements UserVocabularyMediaService {
    private static final String MEDIA_TYPE_IMAGE = "IMAGE";
    private static final String SOURCE_TYPE_USER = "USER";

    private final UserVocabularyRepository vocabularyRepository;
    private final UserVocabularyMediaRepository mediaRepository;
    private final StorageProvider storageProvider;
    private final StorageProperties storageProperties;

    @Override
    public String uploadPending(Long userId, MultipartFile file) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        validateFile(file);
        String objectKey = objectKey(userId, file.getOriginalFilename(), file.getContentType());
        try {
            storageProvider.upload(objectKey, file.getContentType(), file.getInputStream(), file.getSize());
            return storageProvider.getUrl(objectKey);
        } catch (IOException | RuntimeException exception) {
            try {
                storageProvider.delete(objectKey);
            } catch (RuntimeException cleanupException) {
                exception.addSuppressed(cleanupException);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", exception);
        }
    }

    @Override
    public void attachToVocabulary(UserVocabulary vocabulary, String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }
        String objectKey;
        try {
            objectKey = storageProvider.resolveObjectKey(imageUrl);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid imageUrl", exception);
        }
        String expectedPrefix = "user-vocab/" + vocabulary.getUserId() + "/";
        if (!objectKey.startsWith(expectedPrefix) || objectKey.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid imageUrl");
        }
        mediaRepository.save(UserVocabularyMedia.builder()
                .userVocabulary(vocabulary)
                .mediaUrl(objectKey)
                .mediaType(MEDIA_TYPE_IMAGE)
                .sourceType(SOURCE_TYPE_USER)
                .primary(true)
                .build());
    }

    @Transactional
    @Override
    public UserVocabularyMedia upload(Long vocabularyId, Long userId, MultipartFile file) {
        validateFile(file);
        UserVocabulary vocabulary = getVocabulary(vocabularyId, userId);
        String objectKey = objectKey(userId, file.getOriginalFilename(), file.getContentType());
        try {
            storageProvider.upload(objectKey, file.getContentType(), file.getInputStream(), file.getSize());
            return saveMedia(vocabulary, objectKey, true);
        } catch (IOException | RuntimeException exception) {
            storageProvider.delete(objectKey);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store image", exception);
        }
    }

    @Override
    public PresignedUpload prepareUpload(Long vocabularyId, PrepareVocabularyMediaUploadRequest request) {
        validateRequest(request);
        getVocabulary(vocabularyId, request.getUserId());
        validateImage(request.getContentType(), request.getContentLength());
        String objectKey = objectKey(request.getUserId(), null, request.getContentType());
        return storageProvider.createUploadUrl(objectKey, request.getContentType(), request.getContentLength());
    }

    @Transactional
    @Override
    public UserVocabularyMedia completeUpload(Long vocabularyId, CompleteVocabularyMediaUploadRequest request) {
        if (request == null || request.getUserId() == null || isBlank(request.getObjectKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and objectKey are required");
        }
        UserVocabulary vocabulary = getVocabulary(vocabularyId, request.getUserId());
        String expectedPrefix = "user-vocab/" + request.getUserId() + "/";
        if (!request.getObjectKey().startsWith(expectedPrefix)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid objectKey");
        }
        return saveMedia(vocabulary, request.getObjectKey(), request.getPrimary() == null || request.getPrimary());
    }

    private UserVocabularyMedia saveMedia(UserVocabulary vocabulary, String objectKey, boolean primary) {
        return mediaRepository.save(UserVocabularyMedia.builder()
                .userVocabulary(vocabulary)
                .mediaUrl(objectKey)
                .mediaType(MEDIA_TYPE_IMAGE)
                .sourceType(SOURCE_TYPE_USER)
                .primary(primary)
                .build());
    }

    private UserVocabulary getVocabulary(Long vocabularyId, Long userId) {
        if (vocabularyId == null || userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "vocabularyId and userId are required");
        }
        return vocabularyRepository.findById(vocabularyId)
                .filter(vocabulary -> userId.equals(vocabulary.getUserId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User vocabulary not found"));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image file is required");
        }
        validateImage(file.getContentType(), file.getSize());
    }

    private void validateRequest(PrepareVocabularyMediaUploadRequest request) {
        if (request == null || request.getUserId() == null || isBlank(request.getContentType()) || request.getContentLength() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId, contentType and contentLength are required");
        }
    }

    private void validateImage(String contentType, long contentLength) {
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are supported");
        }
        if (contentLength <= 0 || contentLength > storageProperties.getMaxFileSizeBytes()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image exceeds the allowed size");
        }
    }

    private String objectKey(Long userId, String originalFilename, String contentType) {
        String extension = extension(originalFilename, contentType);
        return "user-vocab/" + userId + "/" + UUID.randomUUID() + extension;
    }

    private String extension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            return originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase(Locale.ROOT);
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".img";
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}