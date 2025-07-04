package com.lnreddy.WhatsAppClone.message;

import com.lnreddy.WhatsAppClone.chat.Chat;
import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
@NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createDate")
@NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,query = "UPDATE Message SET state=: newState WHERE chat.id =:chaiId")
public class Message extends BaseAuditEntity {

    @Id
    @SequenceGenerator(name = "msg_seq",sequenceName = "msg_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "msg_seq")
    private Long id;

   @Column(columnDefinition = "TEXT")
    private String content;
   @Enumerated(EnumType.STRING)
   private MessageState state;
   @ManyToOne
   @JoinColumn(name = "chat_id")
   private Chat chat;
    @Enumerated(EnumType.STRING)
    private MessageType type;
   @Column(name = "sender_id",nullable = false)
    private String senderId;
    @Column(name = "receiver_id",nullable = false)
    private String receiverId;
}
