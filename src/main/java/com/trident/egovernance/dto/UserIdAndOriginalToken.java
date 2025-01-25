package com.trident.egovernance.dto;

public record UserIdAndOriginalToken(
        String userId,
        String originalToken
) {
}
