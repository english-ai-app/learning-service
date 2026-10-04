package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.domain.dto.VocabularyImageUploadResponse;
import com.EnglishApp.learning_service.service.UserVocabularyMediaService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning/vocabulary-media")
public class VocabularyImageUploadController {
    private final UserVocabularyMediaService mediaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public VocabularyImageUploadResponse upload(
            @RequestHeader("X-Action-User") String actionUserHeader,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            JsonNode actor = objectMapper.readTree(actionUserHeader);
            long userId = actor.path("id").asLong(0);
            if (userId <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authenticated actor has no valid id");
            }
            return new VocabularyImageUploadResponse(mediaService.uploadPending(userId, file));
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (JsonProcessingException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid authenticated actor", exception);
        }
    }
}
