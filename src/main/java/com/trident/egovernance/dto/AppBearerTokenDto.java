package com.trident.egovernance.dto;

import lombok.*;


public record AppBearerTokenDto(
        String token_type,
        long expires_in,
        String ext_expires_in,
        String access_token
) {
}
