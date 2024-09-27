package com.trident.egovernance.helpers;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public enum FeeTypesType implements Serializable {
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
