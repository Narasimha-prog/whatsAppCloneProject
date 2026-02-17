package com.lnreddy.WhatsAppClone.auth.dto;

import lombok.Builder;

@Builder
public record ForgetPasswordRequest(String email) {
}
