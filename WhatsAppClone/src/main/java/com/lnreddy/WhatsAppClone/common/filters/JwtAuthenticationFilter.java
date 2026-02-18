package com.lnreddy.WhatsAppClone.common.filters;


import com.lnreddy.WhatsAppClone.auth.service.JwtService;
import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import com.lnreddy.WhatsAppClone.user.entity.User;
import com.lnreddy.WhatsAppClone.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtUtil;

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
log.info("incoming request for jwtFilter: {}",request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {

                String username = jwtUtil.getUserId(token);

                // 1️⃣ Get domain user
                User domainUser = userService.findByUserId(UUID.fromString(username));

                // 2️⃣ Wrap in UserDetails
                CustomeUserDetails userDetails = new CustomeUserDetails(domainUser);

                // 3️⃣ Set Authentication with proper authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info(
                        "Authentication is success for: {}",
                        request.getRequestURI()
                );
                userDetails.getAuthorities().forEach(a -> log.info(
                        "Granted: {}",
                        a.getAuthority()
                ));
            }
        }
        filterChain.doFilter(request,response);

    }

}
