package com.lnreddy.WhatsAppClone.auth.rest;
import com.lnreddy.WhatsAppClone.auth.dto.AuthUserRequest;
import com.lnreddy.WhatsAppClone.auth.dto.AuthUserResponse;
import com.lnreddy.WhatsAppClone.auth.dto.RegisterUserRequest;
import com.lnreddy.WhatsAppClone.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    // 🔹 Register
    @PostMapping("/register")
    public ResponseEntity<AuthUserResponse> register(
            @Valid @RequestBody RegisterUserRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // 🔹 Login
    @PostMapping("/login")
    public ResponseEntity<AuthUserResponse> login(
            @Valid @RequestBody AuthUserRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}

