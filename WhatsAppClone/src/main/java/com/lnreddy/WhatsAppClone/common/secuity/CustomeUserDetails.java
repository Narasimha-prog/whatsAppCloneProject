package com.lnreddy.WhatsAppClone.common.secuity;

import com.lnreddy.friendlyecommerce.user.domain.model.aggrigate.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CustomeUserDetails implements UserDetails {

    private final transient User user;

    public CustomeUserDetails(User user) {
        this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = user.getRole().stream()
                .map(e -> new SimpleGrantedAuthority("ROLE_" + e.roleStatus().name()))
                .collect(Collectors.toUnmodifiableSet());

        authorities.forEach(a -> log.info("Mapped authority: {}", a.getAuthority()));

        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword().hashed();
    }

    @Override
    public String getUsername() {
        return user.getEmail().value();
    }
}
