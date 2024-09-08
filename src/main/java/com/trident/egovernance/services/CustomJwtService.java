package com.trident.egovernance.services;

import com.trident.egovernance.config.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface CustomJwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims,T> claimsResolver);
    String generateToken(CustomUserDetails customUserDetails);
    String generateToken(Map<String,Object> extractClaims, CustomUserDetails customUserDetails);
    long getExpirationTime();
    boolean isTokenValid(String token, CustomUserDetails customUserDetails);
}
