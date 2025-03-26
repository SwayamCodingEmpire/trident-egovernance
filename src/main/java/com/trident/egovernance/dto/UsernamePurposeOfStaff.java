package com.trident.egovernance.dto;

public record UsernamePurposeOfStaff(
        String staffDesignation,
        String staffRole,
        String staffName
) {
    public UsernamePurposeOfStaff{
        if (staffDesignation == null || staffDesignation.isBlank()) {
            throw new IllegalArgumentException("Staff Designation cannot be null or blank");
        }
        if (staffRole == null || staffRole.isBlank()) {
            throw new IllegalArgumentException("Staff Role cannot be null or blank");
        }
        if (staffName == null || staffName.isBlank()) {
            throw new IllegalArgumentException("Staff Name cannot be null or blank");
        }
    }
}
