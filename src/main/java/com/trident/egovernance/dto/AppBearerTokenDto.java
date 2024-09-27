package com.trident.egovernance.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppBearerTokenDto {
    private String token_type;
    private long expires_in;
    private String ext_expires_in;
    private String access_token;
}
