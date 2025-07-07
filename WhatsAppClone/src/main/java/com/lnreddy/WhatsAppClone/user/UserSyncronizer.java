package com.lnreddy.WhatsAppClone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncronizer {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public void syncronizeWithIdp(Jwt token) {
        log.info("Synchronize user with IDp");

        getUserEmailFromToken(token).ifPresent(

                userMail->{
                    log.info("Synchronize User having email {}",userMail);


                    User user=userMapper.fromTokenAttributes(token.getClaims());
                    userRepository.save(user);

                }
        );
    }

    private Optional<String> getUserEmailFromToken(Jwt token){
        Map<String,Object> attributes=token.getClaims();
        if(attributes.containsKey("email")){
            return Optional.of(attributes.get("email").toString());
        }
       return Optional.empty();
    }

}
