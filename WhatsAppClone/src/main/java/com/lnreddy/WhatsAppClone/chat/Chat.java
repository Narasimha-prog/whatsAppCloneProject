package com.lnreddy.WhatsAppClone.chat;


import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import com.lnreddy.WhatsAppClone.common.User;
import com.lnreddy.WhatsAppClone.message.Message;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")
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
    @OneToMany(mappedBy = "chat",fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<com.lnreddy.WhatsAppClone.message.Message> messages;

}
