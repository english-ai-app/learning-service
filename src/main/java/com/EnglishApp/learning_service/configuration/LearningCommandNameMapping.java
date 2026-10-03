package com.EnglishApp.learning_service.configuration;

import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.command.RollbackCreateUserVocabularyCommand;
import io.eventuate.tram.commands.common.Command;
import io.eventuate.tram.commands.common.CommandNameMapping;
import java.util.Map;

public class LearningCommandNameMapping implements CommandNameMapping {
    private final Map<String, String> types = Map.of(
            CreateUserVocabularyCommand.class.getSimpleName(), CreateUserVocabularyCommand.class.getName(),
            RollbackCreateUserVocabularyCommand.class.getSimpleName(), RollbackCreateUserVocabularyCommand.class.getName());
    public String commandToExternalCommandType(Command command) { return command.getClass().getSimpleName(); }
    public String externalCommandTypeToCommandClassName(String commandType) { return types.getOrDefault(commandType, commandType); }
}
