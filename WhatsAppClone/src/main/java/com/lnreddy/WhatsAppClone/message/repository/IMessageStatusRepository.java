package com.lnreddy.WhatsAppClone.message.repository;

import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IMessageStatusRepository extends JpaRepository<MessageStatus, UUID> {

    // Get all unread messages for a user in a chat
    @Query("SELECT ms.message.id FROM MessageStatus ms " +
            "WHERE ms.user.id = :userId AND ms.message.chat.id = :chatId " +
            "AND ms.state = :state")
    List<UUID> findUnreadMessageIds(
            @Param("userId") UUID userId,
            @Param("chatId") UUID chatId,
            @Param("state") MessageState state);

    // Mark all messages as SEEN for a user in a chat
    @Modifying
    @Query("UPDATE MessageStatus ms SET ms.state = :newState " +
            "WHERE ms.user.id = :userId AND ms.message.chat.id = :chatId")
    void markMessagesAsSeen(
            @Param("userId") UUID userId,
            @Param("chatId") UUID chatId,
            @Param("newState") MessageState newState);


    @Query("""
   SELECT COUNT(ms)
   FROM MessageStatus ms
   WHERE ms.user.id = :userId
   AND ms.message.chat.id = :chatId
   AND ms.state = :state
""")
    Long countUnreadMessages(
            @Param("userId") UUID userId,
            @Param("chatId") UUID chatId,
            @Param("state") MessageState state);

}

