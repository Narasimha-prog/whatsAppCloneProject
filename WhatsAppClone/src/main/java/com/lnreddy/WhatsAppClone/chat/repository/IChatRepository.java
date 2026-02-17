package com.lnreddy.WhatsAppClone.chat.repository;

import com.lnreddy.WhatsAppClone.chat.constants.ChatConstants;
import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IChatRepository extends JpaRepository<Chat, UUID> {
    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID)
    List<Chat> findChatsBySenderId(@Param("senderId") UUID userId);

    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID)
    Optional<Chat> findChatsByReceiverAndSender(@Param("senderId") UUID senderId,@Param("recipientId") UUID receiverId);
}
