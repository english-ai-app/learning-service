package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.storage.StorageProperties;
import com.EnglishApp.learning_service.storage.StorageProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning/profile/avatar")
public class UserProfileImageUploadController {
    private static final Map<String, String> IMAGE_EXTENSIONS = Map.of(
            MediaType.IMAGE_JPEG_VALUE, "jpg",
            MediaType.IMAGE_PNG_VALUE, "png",
            "image/webp", "webp"
    );

    private final StorageProvider storageProvider;
    private final StorageProperties storageProperties;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> upload(
            @RequestHeader("X-Action-User") String actionUserHeader,
            @RequestPart("file") MultipartFile file
    ) {
        long userId = getAuthenticatedUserId(actionUserHeader);
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar image cannot be empty");
        }
        if (file.getSize() > storageProperties.getMaxFileSizeBytes()) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Avatar image is too large");
        }

        String contentType = file.getContentType();
        String extension = IMAGE_EXTENSIONS.get(contentType);
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Avatar must be JPEG, PNG, or WebP");
        }

        String objectKey = "users/" + userId + "/avatar/" + UUID.randomUUID() + "." + extension;
        try {
            storageProvider.upload(
                    objectKey,
                    contentType,
                    file.getInputStream(),
                    file.getSize()
            );
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not upload avatar image", exception);
        }
        return Map.of("imageUrl", storageProvider.getUrl(objectKey));
    }

    private long getAuthenticatedUserId(String actionUserHeader) {
        try {
            JsonNode actor = objectMapper.readTree(actionUserHeader);
            long userId = actor.path("id").asLong(0);
            if (userId <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Authenticated actor has no valid id"
                );
            }
            return userId;
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid authenticated actor", exception);
        }
    }
}
