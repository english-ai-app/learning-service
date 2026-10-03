package com.EnglishApp.learning_service.handler;

import com.EnglishApp.learning_service.domain.command.CreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.command.RollbackCreateUserVocabularyCommand;
import com.EnglishApp.learning_service.domain.message.CreateUserVocabularyFail;
import com.EnglishApp.learning_service.domain.message.CreateUserVocabularySuccess;
import com.EnglishApp.learning_service.domain.model.UserVocabulary;
import com.EnglishApp.learning_service.domain.model.VocabularyCollection;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItem;
import com.EnglishApp.learning_service.domain.model.VocabularyCollectionItemId;
import com.EnglishApp.learning_service.repo.UserVocabularyRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionItemRepository;
import com.EnglishApp.learning_service.repo.VocabularyCollectionRepository;
import io.eventuate.tram.commands.common.*;
import io.eventuate.tram.commands.consumer.*;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.producer.MessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserVocabularyCommandHandler {
    private static final String SUCCESS = "com.EnglishApp.orchestrator.domain.message.learning.CreateUserVocabularySuccess";
    private static final String FAILURE = "com.EnglishApp.orchestrator.domain.message.learning.CreateUserVocabularyFail";
    private final UserVocabularyRepository vocabularyRepository;
    private final VocabularyCollectionRepository collectionRepository;
    private final VocabularyCollectionItemRepository itemRepository;

    public void register(CommandHandlersBuilder builder) {
        builder.onMessage(CreateUserVocabularyCommand.class, this::create);
        builder.onMessage(RollbackCreateUserVocabularyCommand.class, this::rollback);
    }

    @Transactional
    public Message create(CommandMessage<CreateUserVocabularyCommand> message) {
        CreateUserVocabularyCommand command = message.getCommand();
        try {
            if (command.getUserId() == null || command.getWordId() == null) throw new IllegalArgumentException("userId and wordId are required");
            UserVocabulary vocabulary = vocabularyRepository.save(UserVocabulary.builder().userId(command.getUserId()).wordId(command.getWordId()).source(command.getSource() == null ? "MANUAL" : command.getSource()).status((byte) 1).build());
            if (command.getCollectionId() != null) {
                VocabularyCollection collection = collectionRepository.findById(command.getCollectionId())
                        .filter(item -> command.getUserId().equals(item.getUserId()))
                        .orElseThrow(() -> new IllegalArgumentException("Vocabulary collection not found"));
                itemRepository.save(VocabularyCollectionItem.builder().id(new VocabularyCollectionItemId(collection.getId(), vocabulary.getId())).collection(collection).userVocabulary(vocabulary).build());
            }
            return reply(CommandHandlerReplyBuilder.withSuccess(CreateUserVocabularySuccess.builder().userVocabularyId(vocabulary.getId()).build()), SUCCESS);
        } catch (Exception exception) {
            return reply(CommandHandlerReplyBuilder.withFailure(CreateUserVocabularyFail.builder().errorCode("LEARNING_CREATE_USER_VOCABULARY_FAILED").message(exception.getMessage()).build()), FAILURE);
        }
    }

    @Transactional
    public Message rollback(CommandMessage<RollbackCreateUserVocabularyCommand> message) {
        Long id = message.getCommand().getUserVocabularyId();
        if (id != null) {
            itemRepository.deleteAll(itemRepository.findByUserVocabularyId(id));
            vocabularyRepository.deleteById(id);
        }
        return CommandHandlerReplyBuilder.withSuccess(new Success());
    }

    private Message reply(Message message, String type) {
        return MessageBuilder.withMessage(message).withHeader(ReplyMessageHeaders.REPLY_TYPE, type).build();
    }
}
