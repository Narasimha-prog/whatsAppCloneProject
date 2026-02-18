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
    @Query("""
   SELECT DISTINCT c
   FROM Chat c
   JOIN c.participants cu
   WHERE cu.user.id = :userId
   ORDER BY c.createdDate DESC
""")
    List<Chat> findChatsBySenderId(@Param("userId") UUID userId);

}
