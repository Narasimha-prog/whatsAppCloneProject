package com.lnreddy.WhatsAppClone.notification.model;

import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.constants.MessageType;
import com.lnreddy.WhatsAppClone.notification.constants.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private UUID chatId;
    private UUID messageId;
    private UUID senderId;
    private String content;
    private MessageType messageType;
    private NotificationType notificationType; // MESSAGE, IMAGE, SEEN
    private UUID userId; // who saw it
    private List<UUID> seenBy;
    private byte[] media;

}
