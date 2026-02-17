package com.lnreddy.WhatsAppClone.message.repository;

import com.lnreddy.WhatsAppClone.message.constants.MessageConstants;
import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IMessageRepository extends JpaRepository<Message, UUID> {
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") UUID chatId);

    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    @Modifying
    void setMessagesToSeenByChatId(@Param("chatId") UUID chatId,@Param("newState") MessageState messageState);
}
