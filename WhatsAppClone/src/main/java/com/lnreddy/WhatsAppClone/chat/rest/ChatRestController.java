package com.lnreddy.WhatsAppClone.chat.rest;

import com.lnreddy.WhatsAppClone.chat.dto.ChatResponse;
import com.lnreddy.WhatsAppClone.chat.service.ChatService;
import com.lnreddy.WhatsAppClone.common.StringResponse;
import com.lnreddy.WhatsAppClone.common.secuity.CustomeUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chats")
public class ChatRestController {

    private final ChatService chatService;

     @PostMapping
    public ResponseEntity<UUID> createChat(
            @RequestParam("recipient-id") UUID recipientId,
             Authentication authentication
            ){

         UUID chatId = chatService.createChat(authentication, recipientId);

         return ResponseEntity.ok(chatId);

}

@GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(Authentication authentication){

    return ResponseEntity.ok(chatService.getChatByReceiverId(authentication));
}

}
