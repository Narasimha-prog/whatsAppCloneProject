package com.lnreddy.WhatsAppClone.chat;


import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import com.lnreddy.WhatsAppClone.message.MessageState;
import com.lnreddy.WhatsAppClone.message.MessageType;
import com.lnreddy.WhatsAppClone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private String id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @OneToMany(mappedBy = "chat",fetch = FetchType.LAZY)
    @OrderBy("createdDate DESC")
    private List<com.lnreddy.WhatsAppClone.message.Message> messages;

    @Transient
    public String getChatName(String senderId) {
        if (recipient.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public String getChatId() {
        return sender.getId();
    }

    @Transient
    public Long getUnReadMessages(final String senderId){
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
