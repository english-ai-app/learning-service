package com.EnglishApp.learning_service.domain.command;

import io.eventuate.tram.commands.common.Command;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RollbackCreateUserVocabularyCommand implements Command { private Long userVocabularyId; }
