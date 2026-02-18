package com.lnreddy.WhatsAppClone.user.rest;

import com.lnreddy.WhatsAppClone.auth.dto.*;
import com.lnreddy.WhatsAppClone.user.dto.*;
import com.lnreddy.WhatsAppClone.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User")
@Slf4j
public class UserRestController {

    private final UserService userService;

    @GetMapping
    @Operation(security =@SecurityRequirement(name = "jwt") )
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication){

        return ResponseEntity.ok(userService.getAllUsersExceptSelf(authentication));

    }







}
