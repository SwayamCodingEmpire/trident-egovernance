package com.trident.egovernance.global.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MoneyReceiptTokenGeneratorService {
    @Value("${mr.secret.salt}")
    private String SECRET_KEY;// Replace with a strong secret

    // Method to generate the JWT token
    public String generateToken(long number, String paymentReceiver) {
        return Jwts.builder()
                .claim("number", number)
                .claim("paymentReceiver", paymentReceiver)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Method to extract the long number from the JWT token
    public long extractNumber(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("number", Long.class);
    }

    public String extractPaymentReceiver(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("paymentReceiver", String.class);  // Extracts the 'paymentReceiver' claim
    }
}
