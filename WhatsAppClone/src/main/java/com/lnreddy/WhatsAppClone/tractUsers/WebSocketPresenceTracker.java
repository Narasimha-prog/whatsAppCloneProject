package com.lnreddy.WhatsAppClone.tractUsers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketPresenceTracker {

    private final Map<String, LocalDateTime> onlineUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String token = accessor.getFirstNativeHeader("Authorization");
        String userId = extractUserIdFromJwt(token);

        if (userId != null) {
            onlineUsers.put(userId, LocalDateTime.now());
            log.info("WebSocket CONNECTED: {}", userId);
        } else {
            log.warn("WebSocket CONNECTED but userId could not be extracted.");
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String token = accessor.getFirstNativeHeader("Authorization");
        String userId = extractUserIdFromJwt(token);

        if (userId != null) {
            onlineUsers.remove(userId);
            log.info("WebSocket DISCONNECTED: {}", userId);
        }
    }

    public boolean isUserOnline(String userId) {
        return onlineUsers.containsKey(userId);
    }

    private String extractUserIdFromJwt(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) return null;

        try {
            String[] parts = bearerToken.substring(7).split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return payloadJson.replaceAll(".*\"sub\"\\s*:\\s*\"([^\"]+)\".*", "$1"); // crude but effective
        } catch (Exception e) {
            log.error("Failed to decode JWT", e);
            return null;
        }
    }
}
