package com.lnreddy.WhatsAppClone.chat;

import com.lnreddy.WhatsAppClone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatByReceiverId(Authentication currentuser){
        final String userId= currentuser.getName();

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
        return null;
    }


}
