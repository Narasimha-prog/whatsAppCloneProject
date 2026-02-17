package com.lnreddy.WhatsAppClone.auth.dto;

import lombok.Builder;

@Builder
public record ResetPasswordRequest(
        String token,
        String newPassword
) {}
