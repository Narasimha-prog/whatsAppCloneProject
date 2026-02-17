package com.lnreddy.WhatsAppClone.common.dto;

import lombok.Builder;

@Builder
public record UserSummary(String id,
                          String firstName,
                          String lastName,
                          String email) {
}
