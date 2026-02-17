package com.lnreddy.WhatsAppClone.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class JwtService {


    private final Key key;

    private final long expirationMs;


    public JwtService(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expirationMs) {

        this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs=expirationMs;
    }

    // Generate token
    public String generateToken(String uuid, Set<String> roles) {
        return Jwts.builder()
                .setSubject(uuid)
                .claim("roles", String.join(",", roles))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Set<String> getRoles(String token) {
        Set roles = Set.copyOf(parseClaims(token).get(
                "roles",
                Set.class
        ));
        return roles;
    }
    // Validate token
    public boolean validateToken(String token) {
        try {
            parseClaims(token);

            return true;

        }
            catch(ExpiredJwtException e){
                log.warn("Token expired",e);
            }
            catch(JwtException e){
                log.warn("Invalid token",e);
            }
            catch (IllegalArgumentException e){
               log.warn("Invalid arguments: ",e);
            }
            return false;
    }

    // Extract username
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract roles

}
