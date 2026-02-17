package com.lnreddy.WhatsAppClone.user.dto;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse (
      String id,
      String firstName,
      String lastName,
      String email,
      LocalDateTime lastSeen,
      LocalDateTime  isOnline
){


}
