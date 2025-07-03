package com.lnreddy.WhatsAppClone.user;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserMapper {
    public User fromTokenAttributes(Map<String, Object> attributes) {

        User user=new User();

        if(attributes.containsKey("sub")){
            user.setFirstName(attributes.get("sub").toString());
        }else if(attributes.containsKey("nickname")){
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
}
