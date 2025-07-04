package com.lnreddy.WhatsAppClone.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {


    public MessageResponse toMessageREponse(MessageResponse messageResponse) {
        return MessageResponse.builder()
                .id(messageResponse.getId())
                .content(messageResponse.getContent())
                .senderId(messageResponse.getSenderId())
                .receiverId(messageResponse.getReceiverId())
                .type(messageResponse.getType())
                .state(messageResponse.getState())
                .createdAt(messageResponse.getCreatedAt())
                //todo read media file
                .build();
    }

}
