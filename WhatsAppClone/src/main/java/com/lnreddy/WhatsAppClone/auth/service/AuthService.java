package com.lnreddy.WhatsAppClone.auth.service;

import com.lnreddy.WhatsAppClone.common.dto.UserSummary;
import com.lnreddy.WhatsAppClone.user.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lnreddy.WhatsAppClone.auth.dto.*;
import com.lnreddy.WhatsAppClone.user.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 🔹 REGISTER
    public AuthUserResponse register(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of("USER"))
                .build();

       User newUser=  userRepository.save(user);

        String token = jwtService.generateToken(newUser.getId().toString(), user.getRoles());

        return buildAuthResponse(token, user);
    }

    // 🔹 LOGIN
    public AuthUserResponse login(AuthUserRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRoles());

        return buildAuthResponse(token, user);
    }

    // 🔹 Common Response Builder
    private AuthUserResponse buildAuthResponse(String token, User user) {

        return AuthUserResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .issuedAt(Instant.now().toEpochMilli())
                .expiresIn(86400000) // example: 24 hours
                .user(new UserSummary(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail()
                ))
                .build();
    }
}
