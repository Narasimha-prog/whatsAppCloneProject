package com.lnreddy.WhatsAppClone.message.dto;

import com.lnreddy.WhatsAppClone.message.constants.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private UUID senderId;
    private UUID receiverId;
    private MessageType type;
    private UUID chatId;
}
