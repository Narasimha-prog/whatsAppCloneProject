package com.lnreddy.WhatsAppClone.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


public record ForgetPasswordRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email) {
}
