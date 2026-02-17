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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chats")

@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.recipient.id = :senderId ORDER BY createdDate DESC")
@NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id =:senderId  AND c.recipient.id= :recipientId) OR (c.sender.id =:recipientId  AND c.recipient.id=:senderId)"
          )
public class Chat extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Enumerated(EnumType.STRING)
    private ChatType chatType; // PRIVATE / GROUP

    @ManyToMany
    @JoinTable(
            name = "chat_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;

    @OneToMany(mappedBy = "chat",fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private List<Message> messages;



    @Transient
    public String getChatName(UUID senderId) {

        if (recipient.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public String getChatId() {
        return sender.getId().toString();
    }

    @Transient
    public Long getUnReadMessages(final UUID senderId){
        return messages.stream()
                .filter(m->m.getReceiverId().getId().equals(senderId))
                .filter(m-> MessageState.SENT==m.getState())
                .count();
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
    public LocalDateTime getLastMessageTime(){
        if(messages != null && !messages.isEmpty()){
           return messages.get(0).getCreatedDate();
        }
        return null;
    }

}
