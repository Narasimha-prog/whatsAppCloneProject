package com.lnreddy.WhatsAppClone.message.service;

import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import com.lnreddy.WhatsAppClone.chat.repository.IChatRepository;
import com.lnreddy.WhatsAppClone.common.file.FileService;
import com.lnreddy.WhatsAppClone.common.file.FileUtils;
import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.constants.MessageType;
import com.lnreddy.WhatsAppClone.message.dto.MessageRequest;
import com.lnreddy.WhatsAppClone.message.dto.MessageResponse;
import com.lnreddy.WhatsAppClone.message.entity.Message;
import com.lnreddy.WhatsAppClone.message.repository.IMessageRepository;
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
    private final IChatRepository chatRepository;
    private final FileService fileService;
    private final NotificationService notificationService;
    private final IUserRepository userRepository;

    public void saveMessage(MessageRequest messageRequest){

        Chat chat=chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(()->new EntityNotFoundException("Chat was not Found"));
        User sender = userRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender user not found"));

        User receiver = userRepository.findById(  messageRequest.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver user not found"));

        Message message=new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(sender);
        message.setReceiverId(receiver);
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);

        messageRepository.save(message);

        //to_do notification

        Notification notification=Notification.builder()
                                .chatId(chat.getId())
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .notificationType(NotificationType.MESSAGE)
                .chatName(chat.getChatName(messageRequest.getSenderId()))
                                 .build();
        notificationService.sendNotification(message.getReceiverId().getId(),notification);
    }
    public List<MessageResponse> findChatMessages(UUID chatId){

        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(this::toMessageReponse)
                .toList();

    }
@Transactional
    public void setMessagsToSeen(UUID chatId, Authentication authentication){
        Chat chat=chatRepository.findById(chatId)
                .orElseThrow(()->new EntityNotFoundException("Chat is Not Found"));

        final UUID recipientId=getRecipientId(chat, authentication);

        messageRepository.setMessagesToSeenByChatId(chatId,MessageState.SEEN);
        //toDo notification

    Notification notification=Notification.builder()
            .chatId(chat.getId())
            .senderId(getSenderId(chat,authentication))
            .receiverId(recipientId)
            .notificationType(NotificationType.SEEN)
            .build();
    notificationService.sendNotification(recipientId,notification);
    }

    public void uploadMediaMessage(UUID chatId, Authentication authentication, MultipartFile  multipartFile){
        Chat chat=chatRepository.findById(chatId)
                .orElseThrow(()->new EntityNotFoundException("Chat is Not Found"));

        final UUID senderId=getSenderId(chat,authentication);
        final UUID recipientId=getRecipientId(chat,authentication);
        final String filePath=fileService.saveFile(multipartFile,senderId);


        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender user not found"));

        User receiver = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver user not found"));

        Message message=new Message();
        message.setChat(chat);
        message.setSenderId(sender);
        message.setReceiverId(receiver);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        message.setMediaFilePath(filePath);

        messageRepository.save(message);
        //toDo notification
        Notification notification=Notification.builder()
                .chatId(chat.getId())
                .messageType(MessageType.IMAGE)
                .senderId(senderId)
                .receiverId(recipientId)
                .notificationType(NotificationType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();
        notificationService.sendNotification(recipientId,notification);

    }

    private UUID getSenderId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName())){
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    private UUID getRecipientId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName()))
        {
          return   chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }

    public MessageResponse toMessageReponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .senderId(message.getSenderId().getId())
                .receiverId(message.getReceiverId().getId())
                .type(message.getType())
                .state(message.getState())
                .createdAt(message.getCreatedDate())
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath()))
                .build();
    }
}
