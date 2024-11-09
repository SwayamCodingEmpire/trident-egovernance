package com.trident.egovernance.global.helpers;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public enum FeeTypesType implements Serializable {
    COMPULSORY_FEES("COMPULSORY FEES"),
    OPTIONAL_FEES("OPTIONAL FEES"),
    EXCESS_FEE("EXCESS FEE"),
    OTHER_FEES("OTHER FEES");
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
