package com.lnreddy.WhatsAppClone.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseAuditEntity{

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;

}
