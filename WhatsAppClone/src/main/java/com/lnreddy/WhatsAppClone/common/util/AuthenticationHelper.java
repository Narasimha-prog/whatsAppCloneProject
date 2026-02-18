package com.lnreddy.WhatsAppClone.common.util;

import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class AuthenticationHelper {

    private AuthenticationHelper() {
    }

    public static UUID toGetUserId(Authentication authentication){
        CustomeUserDetails userDetails= (CustomeUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
