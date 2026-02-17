package com.lnreddy.WhatsAppClone.common.secuity;


import com.lnreddy.WhatsAppClone.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class CustomeUserDetails implements UserDetails {

    private final transient User user;

    public CustomeUserDetails(User user) {
        this.user=user;
    }

    public UUID getId() {
        return user.getId();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(e -> new SimpleGrantedAuthority("ROLE_" + e))
                .collect(Collectors.toUnmodifiableSet());

        authorities.forEach(a -> log.info("Mapped authority: {}", a.getAuthority()));

        return authorities;
    }

    @Override
    public  String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
