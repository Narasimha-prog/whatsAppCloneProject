package com.lnreddy.WhatsAppClone.chat.entity;


import com.lnreddy.WhatsAppClone.chat.constants.ChatConstants;
import com.lnreddy.WhatsAppClone.chat.constants.ChatType;
import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import com.lnreddy.WhatsAppClone.message.constants.MessageState;
import com.lnreddy.WhatsAppClone.message.constants.MessageType;
import com.lnreddy.WhatsAppClone.message.entity.Message;
import com.lnreddy.WhatsAppClone.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chats")

//@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
//        query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id =:senderId  AND c.recipient.id= :recipientId) OR (c.sender.id =:recipientId  AND c.recipient.id=:senderId)"
//          )
public class Chat extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;     //id


    @Enumerated(EnumType.STRING)
    private ChatType chatType;   // PRIVATE / GROUP


    @Column(nullable = true)  // Only used for group chats
    private String groupName;   // Optional name for group chat

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatUser> participants;  //ADMIN & MEMBERS



    @OneToMany(mappedBy = "chat",fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private List<Message> messages;     //MESSAGES


    @Transient
    public String getChatName(UUID senderId) {

        if (chatType == ChatType.PRIVATE) {

            // Private chat: show the other user's name
            return participants.stream()
                    .map(ChatUser::getUser)
                    .filter(u -> !u.getId().equals(senderId))
                    .findFirst()
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .orElse("Unknown");
        }
        else {
           // For group chat, use groupName if set, otherwise fallback
            return groupName != null ? groupName : "Group Chat (" + participants.size() + " members)";
        }



    }







    @Transient
    public String getLastMessage(){
        if(messages != null && !messages.isEmpty()){
            if(messages.get(0).getType() != MessageType.TEXT){
                return "Attachment";
            }
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public Instant getLastMessageTime(){
        if(messages != null && !messages.isEmpty()){
           return messages.get(0).getCreatedDate();
        }
        return null;
    }

    @Transient
    public List<UUID> getParticipantsExcludeCurrentUser(UUID currentUserId) {

        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return participants.stream()
                .map(ChatUser::getUser)
                .map(User::getId)
                .filter(id -> !id.equals(currentUserId))
                .toList();
    }

}
