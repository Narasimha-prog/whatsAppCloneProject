package com.lnreddy.WhatsAppClone.chat;

import com.lnreddy.WhatsAppClone.common.StringResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chats")
public class ChatController {

    private final ChatService chatService;
@PostMapping
    public ResponseEntity<StringResponse> createChat(
            @RequestParam("sender-id") String senderId
            ,@RequestParam("recipient-id") String recipientId
            ){
    final String chaatId= chatService.createChat(senderId,recipientId);

    StringResponse response=StringResponse.builder()
                              .reponse(chaatId)
                               .build();

    return ResponseEntity.ok(response);
}

@GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(Authentication authentication){

    return ResponseEntity.ok(chatService.getChatByReceiverId(authentication));
}

}
