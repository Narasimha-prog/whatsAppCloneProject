package com.lnreddy.WhatsAppClone.message.service;

import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import com.lnreddy.WhatsAppClone.chat.entity.ChatUser;
import com.lnreddy.WhatsAppClone.chat.repository.IChatRepository;
import com.lnreddy.WhatsAppClone.common.file.FileService;
import com.lnreddy.WhatsAppClone.common.file.FileUtils;
import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import com.lnreddy.WhatsAppClone.common.util.AuthenticationHelper;
import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.constants.MessageType;
import com.lnreddy.WhatsAppClone.message.dto.MessageRequest;
import com.lnreddy.WhatsAppClone.message.dto.MessageResponse;
import com.lnreddy.WhatsAppClone.message.entity.Message;
import com.lnreddy.WhatsAppClone.message.entity.MessageStatus;
import com.lnreddy.WhatsAppClone.message.repository.IMessageRepository;
import com.lnreddy.WhatsAppClone.message.repository.IMessageStatusRepository;
import com.lnreddy.WhatsAppClone.notification.model.Notification;
import com.lnreddy.WhatsAppClone.notification.service.NotificationService;
import com.lnreddy.WhatsAppClone.notification.constants.NotificationType;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final IMessageRepository messageRepository;
    private final IMessageStatusRepository messageStatusRepository;
    private final IChatRepository chatRepository;
    private final FileService fileService;
    private final NotificationService notificationService;
    private final IUserRepository userRepository;

    /* ==========================
       SAVE TEXT MESSAGE
       ========================== */
    @Transactional
    public void saveMessage(MessageRequest messageRequest,Authentication authentication)  {

        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        User sender = userRepository.findById(AuthenticationHelper.toGetUserId(authentication))
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSender(sender);
        message.setType(messageRequest.getType());

        Message savedMessage = messageRepository.save(message);

        // Create MessageStatus for each participant except sender
        List<User> recipients = chat.getParticipants().stream()
                .map(ChatUser::getUser)
                .filter(user -> !user.getId().equals(sender.getId()))
                .toList();

        recipients.forEach(user -> {
            MessageStatus status = new MessageStatus();
            status.setMessage(savedMessage);
            status.setUser(user);
            status.setState(MessageState.SENT);
            messageStatusRepository.save(status);

            // Send notification
            Notification notification = Notification.builder()
                    .chatId(chat.getId())
                    .content(messageRequest.getContent()) //message hi...
                    .messageType(messageRequest.getType())  //TXT
                    .senderId(sender.getId())
                    .notificationType(NotificationType.MESSAGE)
                    .build();

            notificationService.sendNotification(user.getId(), notification);
        });
    }

    /* ==========================
       FETCH CHAT MESSAGES
       ========================== */
    @Transactional(readOnly = true)
    public List<MessageResponse> findChatMessages(UUID chatId, Authentication authentication) {

        UUID currentUserId =
                ((CustomeUserDetails) authentication.getPrincipal()).getId();

        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(message -> toMessageResponse(message, currentUserId))
                .toList();
    }

    /* ==========================
       MARK MESSAGES AS SEEN
       ========================== */
    @Transactional
    public void setMessagesToSeen(UUID chatId, Authentication authentication) {

        UUID currentUserId =
                ((CustomeUserDetails) authentication.getPrincipal()).getId();

        messageStatusRepository.markMessagesAsSeen(
                currentUserId,
                chatId,
                MessageState.SEEN
        );
    }

    /* ==========================
       UPLOAD MEDIA MESSAGE
       ========================== */
    @Transactional
    public void uploadMediaMessage(UUID chatId,
                                   Authentication authentication,
                                   MultipartFile multipartFile) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        UUID senderId =
                ((CustomeUserDetails) authentication.getPrincipal()).getId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        String filePath = fileService.saveFile(multipartFile, senderId);

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setType(MessageType.IMAGE);
        message.setMediaFilePath(filePath);

        Message savedMessage = messageRepository.save(message);

        List<User> recipients = chat.getParticipants().stream()
                .map(ChatUser::getUser)
                .filter(user -> !user.getId().equals(senderId))
                .toList();

        recipients.forEach(user -> {

            MessageStatus status = new MessageStatus();
            status.setMessage(savedMessage);
            status.setUser(user);
            status.setState(MessageState.SENT);
            messageStatusRepository.save(status);

            Notification notification = Notification.builder()
                    .chatId(chat.getId())
                    .messageType(MessageType.IMAGE)
                    .senderId(senderId)
                    .notificationType(NotificationType.IMAGE)
                    .media(FileUtils.readFileFromLocation(filePath))
                    .build();

            notificationService.sendNotification(user.getId(), notification);
        });
    }

    /* ==========================
       CONVERT TO DTO
       ========================== */
    private MessageResponse toMessageResponse(Message message, UUID currentUserId) {

        MessageState state = message.getStatuses().stream()
                .filter(status -> status.getUser().getId().equals(currentUserId))
                .map(MessageStatus::getState)
                .findFirst()
                .orElse(MessageState.SENT);

        List<UUID> seen=message.getStatuses().stream()
                .filter(status-> status.getState()==MessageState.SEEN)
                .map(status->status.getUser().getId())
                .toList();

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSender().getId())
                .type(message.getType())
                .createdAt(message.getCreatedDate())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .seenBy(seen)
                .build();
    }
}
