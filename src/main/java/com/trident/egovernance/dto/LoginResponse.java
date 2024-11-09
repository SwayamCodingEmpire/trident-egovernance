package com.trident.egovernance.dto;

import lombok.*;


public record LoginResponse(
        String token,
        long expiresIn
) {
}
