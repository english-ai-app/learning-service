package com.EnglishApp.learning_service.controller;

import com.EnglishApp.learning_service.domain.dto.CreateVocabularyCollectionRequest;
import com.EnglishApp.learning_service.domain.dto.VocabularyCollectionDTO;
import com.EnglishApp.learning_service.service.VocabularyCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = {"/api/learning/collections", "/api/learning/topics"},
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class VocabularyCollectionController {
    private final VocabularyCollectionService collectionService;

    @GetMapping
    public List<VocabularyCollectionDTO> getCollections(@RequestParam Long userId) {
        return collectionService.getCollections(userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VocabularyCollectionDTO createCollection(@RequestBody CreateVocabularyCollectionRequest request) {
        return collectionService.createCollection(request);
    }
}
