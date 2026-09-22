package com.EnglishApp.learning_service.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class VocabularyCollectionItemId implements Serializable {
    @Column(name = "collection_id")
    private Long collectionId;

    @Column(name = "user_vocabulary_id")
    private Long userVocabularyId;
}
