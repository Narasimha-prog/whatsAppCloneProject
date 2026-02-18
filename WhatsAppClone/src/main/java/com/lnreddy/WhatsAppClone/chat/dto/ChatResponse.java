package com.lnreddy.WhatsAppClone.chat.dto;

import com.lnreddy.WhatsAppClone.chat.constants.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    private UUID id;                     // Chat ID
    private String name;                 // Chat name (user name or group name)
    private ChatType chatType;           // PRIVATE or GROUP
    private Long unreadCount;            // Unread messages for current user
    private String lastMessage;          // Last message content
    private Instant lastMessageTime;     // Timestamp of last message
    private List<UUID> participantIds;   // All participant IDs (useful for groups)

}

