package com.lnreddy.WhatsAppClone.chat.service;

import com.lnreddy.WhatsAppClone.chat.constants.ChatUserRole;
import com.lnreddy.WhatsAppClone.chat.constants.ChatType;
import com.lnreddy.WhatsAppClone.chat.dto.ChatResponse;
import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import com.lnreddy.WhatsAppClone.chat.entity.ChatUser;
import com.lnreddy.WhatsAppClone.chat.repository.IChatRepository;
import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import com.lnreddy.WhatsAppClone.common.util.AuthenticationHelper;
import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.repository.IMessageStatusRepository;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final IChatRepository chatRepository;
    private final IUserRepository userRepository;
    private final IMessageStatusRepository messageStatusRepository;


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

    @Transactional
    public UUID createChat(Authentication authentication, List<UUID> participantIds, String groupName) {


       //to get user Id
        UUID creatorId = AuthenticationHelper.toGetUserId(authentication);

        // Fetch all users including creator
        List<User> users = userRepository.findAllById(participantIds);


        User creator = userRepository.findByPublicId(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Creator user not found"));

        // Create new Chat entity
        Chat chat = new Chat();
        chat.setChatType(participantIds.size() > 1 ? ChatType.GROUP : ChatType.PRIVATE);
        chat.setGroupName(groupName); // null for private chat

        // Create ChatUser objects with roles
        List<ChatUser> chatUsers = users.stream()
                .map(user -> {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setUser(user);
                    chatUser.setChat(chat);
                    chatUser.setRole(user.getId().equals(creatorId) ? ChatUserRole.ADMIN : ChatUserRole.MEMBER);
                    return chatUser;
                }).toList();

        chat.setParticipants(chatUsers);

        Chat savedChat = chatRepository.save(chat);

        return savedChat.getId();
    }


    public ChatResponse toChatResponse(Chat chat, UUID currentUserId){


        return ChatResponse.builder()
                .id(chat.getId())
                .name(chat.getChatName(currentUserId))
                .chatType(chat.getChatType())
                .unreadCount(messageStatusRepository.countUnreadMessages(currentUserId, chat.getId(),
                                                                         MessageState.SENT))
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime())
                .participantIds(chat.getParticipantsExcludeCurrentUser(currentUserId))
                .build();
    }
}
