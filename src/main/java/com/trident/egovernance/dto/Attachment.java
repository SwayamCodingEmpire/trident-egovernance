package com.trident.egovernance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Attachment(
        @JsonProperty("@odata.type") String odataType,
        String name,
        String contentBytes
) {
}
