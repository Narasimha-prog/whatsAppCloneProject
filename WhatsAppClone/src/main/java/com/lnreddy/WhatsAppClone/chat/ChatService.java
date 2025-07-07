package com.lnreddy.WhatsAppClone.chat;

import com.lnreddy.WhatsAppClone.user.User;
import com.lnreddy.WhatsAppClone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatByReceiverId(Authentication currentuser){
        final String userId= currentuser.getName();
        log.info("Current users who log in {}",currentuser.getName());
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat->chatMapper.toChatResponse(chat,userId))
                .toList();

    }

    @Transactional()
    public String  createChat(String senderId,String receiverId){
        Optional<Chat> existedChat=chatRepository.findChatsByReceiverAndSender(senderId,receiverId);
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


}
