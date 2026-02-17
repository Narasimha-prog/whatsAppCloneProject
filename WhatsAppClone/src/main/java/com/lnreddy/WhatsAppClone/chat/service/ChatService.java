package com.lnreddy.WhatsAppClone.chat.service;

import com.lnreddy.WhatsAppClone.chat.dto.ChatResponse;
import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import com.lnreddy.WhatsAppClone.chat.repository.IChatRepository;
import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final IChatRepository chatRepository;
    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatByReceiverId(Authentication currentuser){

        CustomeUserDetails userDetails =
                (CustomeUserDetails) currentuser.getPrincipal();

        final UUID userId= userDetails.getId();
        log.info("Current users who log in {}",currentuser.getName());
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat->this.toChatResponse(chat,userId))
                .toList();

    }

    @Transactional()
    public UUID  createChat(Authentication authentication,UUID receiverId){

        UUID senderId =
                ((CustomeUserDetails) authentication.getPrincipal()).getId();
        Optional<Chat> existedChat=chatRepository.findChatsByReceiverAndSender(senderId, receiverId);

        if(existedChat.isPresent()){
            return existedChat.get().getId();
        }
        User sender=userRepository.findByPublicId(senderId)

                .orElseThrow(()->new EntityNotFoundException("User with Id "+senderId+"Not Found"));

        User recipient=userRepository.findByPublicId(receiverId)
                .orElseThrow(()->new EntityNotFoundException("User with Id "+receiverId+"Not Found"));

        Chat newChat=new Chat();
        newChat.setSender(sender);
        newChat.setRecipient(recipient);

        Chat savedChat=chatRepository.save(newChat);

        return savedChat.getId();
    }

    public ChatResponse toChatResponse(Chat chat, UUID senderId){
        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(senderId))
                .unreadCount(chat.getUnReadMessages(senderId))
                .lastMessage(chat.getLastMessage())
                .isRecipientIsOnline(chat.getRecipient().isUserOnline())
                .senderId(chat.getSender().getId())
                .recipientId(chat.getRecipient().getId())
                .lastMessageTime(chat.getLastMessageTime())
                .build();
    }
}
