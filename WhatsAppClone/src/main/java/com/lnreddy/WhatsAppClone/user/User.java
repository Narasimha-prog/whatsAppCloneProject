package com.lnreddy.WhatsAppClone.user;


import com.lnreddy.WhatsAppClone.chat.Chat;
import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "users")
@NamedQuery(name = UserConstants.FIND_USER_BY_EMAIL,query = "SELECT u FROM User u WHERE u.email = :email ")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,query = "SELECT u FROM User u WHERE u.id != :publicId")
@NamedQuery(name = UserConstants.FIND_USER_BY_PUBLIC_ID,query = "SELECT u FROM User u WHERE u.id = :publicId")
public class User extends BaseAuditEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;
    @OneToMany(mappedBy = "sender")
    private List<Chat> chatAsSender;
    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatAsRecipient;

    public boolean isUserOnline(){
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }

}
