package com.EnglishApp.learning_service.handler;

import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.command.RollbackCreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.message.CreateUserVocabularyFail;
import com.EnglishApp.learning_service.domain.message.CreateUserVocabularySuccess;
import com.EnglishApp.learning_service.service.UserVocabularyService;
import io.eventuate.tram.commands.common.*;
import io.eventuate.tram.commands.consumer.*;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.producer.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVocabularyCommandHandler {
    private static final String SUCCESS = "com.EnglishApp.orchestrator.domain.message.learning.CreateUserVocabularySuccess";
    private static final String FAILURE = "com.EnglishApp.orchestrator.domain.message.learning.CreateUserVocabularyFail";
    private final UserVocabularyService vocabularyService;

    public void register(CommandHandlersBuilder builder) {
        builder.onMessage(CreateUserVocabularyCommand.class, this::create);
        builder.onMessage(RollbackCreateUserVocabularyCommand.class, this::rollback);
    }

    public Message create(CommandMessage<CreateUserVocabularyCommand> message) {
        try {
            Long userVocabularyId = vocabularyService.create(message.getCommand());
            return reply(CommandHandlerReplyBuilder.withSuccess(CreateUserVocabularySuccess.builder()
                    .userVocabularyId(userVocabularyId).build()), SUCCESS);
        } catch (Exception exception) {
            return reply(CommandHandlerReplyBuilder.withFailure(CreateUserVocabularyFail.builder().errorCode("LEARNING_CREATE_USER_VOCABULARY_FAILED").message(exception.getMessage()).build()), FAILURE);
        }
    }

    public Message rollback(CommandMessage<RollbackCreateUserVocabularyCommand> message) {
        vocabularyService.rollback(message.getCommand().getUserVocabularyId());
        return CommandHandlerReplyBuilder.withSuccess(new Success());
    }

    private Message reply(Message message, String type) {
        return MessageBuilder.withMessage(message).withHeader(ReplyMessageHeaders.REPLY_TYPE, type).build();
    }
}
