package com.lnreddy.WhatsAppClone.chat.entity;

import com.lnreddy.WhatsAppClone.chat.constants.ChatUserRole;
import com.lnreddy.WhatsAppClone.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_users")
public class ChatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ChatUserRole role; // ADMIN / MEMBER
}



