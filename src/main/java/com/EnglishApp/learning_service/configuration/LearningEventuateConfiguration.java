package com.EnglishApp.learning_service.configuration;

import com.EnglishApp.learning_service.handler.UserVocabularyCommandHandler;
import io.eventuate.tram.commands.common.CommandNameMapping;
import io.eventuate.tram.commands.consumer.*;
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector;
import org.springframework.context.annotation.*;
import java.util.List;

@Configuration
public class LearningEventuateConfiguration {
    public static final String LEARNING_CHANNEL = "learning-service";
    @Bean @Primary public CommandNameMapping learningCommandNameMapping() { return new LearningCommandNameMapping(); }
    @Bean public io.eventuate.tram.consumer.common.DuplicateMessageDetector learningDuplicateDetector() { return new NoopDuplicateMessageDetector(); }
    @Bean public CommandHandlers learningCommandHandlers(List<UserVocabularyCommandHandler> handlers) {
        CommandHandlersBuilder builder = CommandHandlersBuilder.fromChannel(LEARNING_CHANNEL);
        handlers.forEach(handler -> handler.register(builder));
        return builder.build();
    }
    @Bean public CommandDispatcher learningCommandDispatcher(CommandDispatcherFactory factory, CommandHandlers handlers) {
        return factory.make("learningCommandDispatcher", handlers);
    }
}
