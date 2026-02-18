package com.lnreddy.WhatsAppClone.user.mapper;

import com.lnreddy.WhatsAppClone.user.dto.UserResponse;
import com.lnreddy.WhatsAppClone.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class UserMapper {
    public User fromTokenAttributes(Map<String, Object> attributes) {

        User user=new User();

        if(attributes.containsKey("sub")){
            user.setId(UUID.fromString((String)attributes.get("sub")));
        }

        if (attributes.containsKey("given_name")) {
            user.setFirstName(attributes.get("given_name").toString());
        } else if (attributes.containsKey("nickname")) {
            user.setFirstName(attributes.get("nickname").toString());
        }
        if(attributes.containsKey("family_name")){
            user.setLastName(attributes.get("family_name").toString());
        }

        if(attributes.containsKey("email")){
            user.setEmail(attributes.get("email").toString());
        }
        user.setLastSeen(LocalDateTime.now());

        return user;
    }

    public UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .lastSeen(user.getLastSeen())
                .isOnline(user.isUserOnline())
                .build();

    }
}
