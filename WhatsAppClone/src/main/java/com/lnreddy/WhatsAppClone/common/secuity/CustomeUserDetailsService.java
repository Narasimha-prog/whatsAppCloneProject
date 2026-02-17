package com.lnreddy.WhatsAppClone.common.secuity;

import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetailsService;

@Component
@RequiredArgsConstructor
public class CustomeUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public @NonNull UserDetails loadUserByUsername(@org.springframework.lang.NonNull String email) throws UsernameNotFoundException {
        User user = userService.findByUserEmailId(email);
        return new CustomeUserDetails(user);
    }
}
