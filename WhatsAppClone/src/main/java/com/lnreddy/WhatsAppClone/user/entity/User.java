package com.lnreddy.WhatsAppClone.user.entity;


import com.lnreddy.WhatsAppClone.chat.entity.Chat;
import com.lnreddy.WhatsAppClone.chat.entity.ChatUser;
import com.lnreddy.WhatsAppClone.common.BaseAuditEntity;
import com.lnreddy.WhatsAppClone.user.constants.UserConstants;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.lnreddy.WhatsAppClone.user.constants.UserConstants.FIND_USER_BY_EMAIL;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@NamedQuery(name = FIND_USER_BY_EMAIL,query = "SELECT u FROM User u WHERE u.email = :email ")
@NamedQuery(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,query = "SELECT u FROM User u WHERE u.id != :publicId")
@NamedQuery(name = UserConstants.FIND_USER_BY_PUBLIC_ID,query = "SELECT u FROM User u WHERE u.id = :publicId")
public class User extends BaseAuditEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Set<String> roles;

    private String phoneNumber;

    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "user")
    private List<ChatUser> chatUsers;


    public boolean isUserOnline(){
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }

}
