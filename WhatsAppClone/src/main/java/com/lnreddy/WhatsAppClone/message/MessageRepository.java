package com.lnreddy.WhatsAppClone.message;

import com.lnreddy.WhatsAppClone.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<MessageResponse> findMessagesByChatId(@Param("chatId") String chatId);

    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    void setMessagesToSeenByChatId(Chat chat, MessageState messageState);
}
