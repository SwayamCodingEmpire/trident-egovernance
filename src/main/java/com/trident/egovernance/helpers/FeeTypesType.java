package com.trident.egovernance.helpers;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum FeeTypesType {
    COMPULSORY_FEES("COMPULSORY FEES"),
    OPTIONAL_FEES("OPTIONAL FEES");
    private final String displayName;

    public static FeeTypesType fromDisplayName(String displayName) {
        for (FeeTypesType type : FeeTypesType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        return null;
    }
    FeeTypesType(String displayName) {
        this.displayName = displayName;
    }
}
