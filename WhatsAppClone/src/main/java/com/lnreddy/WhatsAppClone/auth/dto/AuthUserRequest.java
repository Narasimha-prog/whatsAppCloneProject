package com.lnreddy.WhatsAppClone.auth.dto;

public record AuthUserRequest(
        String email,
        String password
) {
}
