package com.trident.egovernance.global.helpers;

import java.io.Serializable;

public enum BooleanString implements Serializable {
    YES,
    NO;

    public static BooleanString fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null; // Handle null/empty gracefully
        }
        try {
            // Convert input to uppercase before valueOf() lookup
            return BooleanString.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            // Optionally log the error here if the value is truly unexpected
            throw new IllegalArgumentException("Invalid BooleanString value: " + text, e);
        }
    }
}
