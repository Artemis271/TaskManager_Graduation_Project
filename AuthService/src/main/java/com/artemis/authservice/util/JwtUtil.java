package com.artemis.authservice.util;

import com.artemis.authservice.models.AuthValidationResponse;
import com.artemis.authservice.models.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil
{
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    public static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey secretKey;

    public String generateToken(UserDetails user)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());
        if (user instanceof MyUserDetails)
            claims.put("id", ((MyUserDetails) user).getId());
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .signWith(secretKey)
                .compact();
    }

    public String getUsername(String token)
    {
        return getAllClaims(token).getSubject();
    }

    public List<String> getRoles(String token)
    {
        return getAllClaims(token).get("roles", List.class);
    }

    public Claims getAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token)
    {
        return getAllClaims(token).get("id", Long.class);
    }

    public AuthValidationResponse isValidToken(String authHeader)
    {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX))
            return AuthValidationResponse.builder().valid(false).build();

        String token = authHeader.substring(BEARER_PREFIX.length());
        try
        {
            Claims claims = getAllClaims(token);
            return AuthValidationResponse.builder()
                    .valid(true)
                    .userId(claims.get("id", Long.class))
                    .username(claims.getSubject())
                    .roles(claims.get("roles", List.class))
                    .build();
        } catch (JwtException | IllegalArgumentException exc)
        {
            return AuthValidationResponse.builder().valid(false).build();
        }
    }
}