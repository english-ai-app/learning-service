package com.EnglishApp.learning_service.domain.message;

import lombok.*;
import java.io.Serializable;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateUserVocabularySuccess implements Serializable { private Long userVocabularyId; }
