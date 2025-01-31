package com.trident.egovernance.global.services;

import com.trident.egovernance.config.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomJwtServiceImpl implements CustomJwtService {
    @Value("${jwt.secret.key}")
    private String secretKey;
    private final long jwtExpiration = 36000000;
    @Override
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(CustomUserDetails customUserDetails) {
        return generateToken(new HashMap<>(),customUserDetails);
    }

    @Override
    public String generateToken(Map<String, Object> extractClaims, CustomUserDetails customUserDetails) {
        List<String> roles = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Ensure all roles have the "ROLE_" prefix
        roles = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());

        // Optionally, add "ROLE_NSR" if it's not already present
        if (!roles.contains("ROLE_NSR")) {
            roles.add("ROLE_NSR");
        }

        extractClaims.put("roles", roles);
        return buildToken(extractClaims, customUserDetails, jwtExpiration);
    }
    private String buildToken(Map<String,Object> extractClaims, CustomUserDetails customUserDetails, long jwtExpiration){
        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public long getExpirationTime() {
        return jwtExpiration;
    }

    @Override
    public boolean isTokenValid(String token, CustomUserDetails customUserDetails) {
        final String username = extractUsername(token);
        return (username.equals(customUserDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
