package com.EnglishApp.learning_service.domain.command;

import io.eventuate.tram.commands.common.Command;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateUserVocabularyCommand implements Command {
    private Long userId;
    private Long wordId;
    private Long collectionId;
    private String source;
}
