package com.lnreddy.WhatsAppClone.common.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserSummary(UUID id,
                          String firstName,
                          String lastName,
                          String email) {
}
