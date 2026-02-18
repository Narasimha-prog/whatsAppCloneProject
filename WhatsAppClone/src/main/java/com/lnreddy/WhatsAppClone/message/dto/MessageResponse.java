package com.lnreddy.WhatsAppClone.message.dto;

import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.constants.MessageType;
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
public class MessageResponse {

    private UUID id;
    private String content;
    private MessageType type;
    private MessageState state;
    private UUID senderId;
    private List<UUID> receiverIds;
    private Instant createdAt;
    private byte[] media;
}


