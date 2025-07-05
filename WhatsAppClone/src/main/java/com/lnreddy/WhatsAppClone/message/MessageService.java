package com.lnreddy.WhatsAppClone.message;

import com.lnreddy.WhatsAppClone.chat.Chat;
import com.lnreddy.WhatsAppClone.chat.ChatRepository;
import com.lnreddy.WhatsAppClone.file.FileService;
import com.lnreddy.WhatsAppClone.file.FileUtils;
import com.lnreddy.WhatsAppClone.notification.Notification;
import com.lnreddy.WhatsAppClone.notification.NotificationService;
import com.lnreddy.WhatsAppClone.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest){

        Chat chat=chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(()->new EntityNotFoundException("Chat was not Found"));

        Message message=new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());
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
        notificationService.sendNotification(message.getReceiverId(),notification);
    }
    public List<MessageResponse> findChatMessages(String chatId){

        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageReponse)
                .toList();

    }
@Transactional
    public void setMessagsToSeen(String chatId, Authentication authentication){
        Chat chat=chatRepository.findById(chatId)
                .orElseThrow(()->new EntityNotFoundException("Chat is Not Found"));

        final String recipientId=getRecipientId(chat, authentication);

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

    public void uploadMediaMessage(String chatId, Authentication authentication, MultipartFile  multipartFile){
        Chat chat=chatRepository.findById(chatId)
                .orElseThrow(()->new EntityNotFoundException("Chat is Not Found"));

        final String senderId=getSenderId(chat,authentication);
        final String recipientId=getRecipientId(chat,authentication);
        final String filePath=fileService.saveFile(multipartFile,senderId);


        Message message=new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverId(recipientId);
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

    private String getSenderId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName())){
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    private String getRecipientId(Chat chat, Authentication authentication) {
        if(chat.getSender().getId().equals(authentication.getName()))
        {
          return   chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }


}
