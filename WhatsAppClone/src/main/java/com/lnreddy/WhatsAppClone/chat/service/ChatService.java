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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final IChatRepository chatRepository;
    private final IUserRepository userRepository;
    private final IMessageStatusRepository messageStatusRepository;


    @Transactional(readOnly = true)
    public List<ChatResponse> getChatByCurrentId(Authentication currentUser){


       //get current user Id
        final UUID userId= AuthenticationHelper.toGetUserId(currentUser);

        log.info("Current users who log in {}",currentUser.getName());

        //gets chats based on current user
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat->this.toChatResponse(chat,userId))
                .toList();

    }

    @Transactional
    public UUID createChat(Authentication authentication, List<UUID> participantIds, String groupName) {


       //to get user id
        UUID creatorId = AuthenticationHelper.toGetUserId(authentication);

        //to add current user he is creating chat
        List<UUID> allParticipants = new ArrayList<>(participantIds);
        if (!allParticipants.contains(creatorId)) {
            allParticipants.add(creatorId);
        }

        // Fetch all users including creator
        List<User> users = userRepository.findAllById(allParticipants);

        if(users.size()==2){
            Optional<Chat> existing =
                    chatRepository.findPrivateChatBetween(users.get(0).getId(),users.get(1).getId());
            if (existing.isPresent()) {
                return existing.get().getId();
            }
        }

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
                .unreadCount(messageStatusRepository.countUnreadMessages(currentUserId, chat.getId(), MessageState.SENT))
                .lastMessage(chat.getLastMessage())
                .lastMessageTime(chat.getLastMessageTime())
                .participantIds(chat.getParticipantsExcludeCurrentUser(currentUserId))
                .build();
    }
}
