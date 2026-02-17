package com.lnreddy.WhatsAppClone.auth.dto;

import com.lnreddy.WhatsAppClone.user.dto.UserSummary;
import lombok.Builder;

@Builder
public record AuthUserResponse(String accessToken,
                               String tokenType,
                               long expiresIn,    // duration in ms
                               long issuedAt ,
                               UserSummary user// creation time in epoch ms
 ) {

}