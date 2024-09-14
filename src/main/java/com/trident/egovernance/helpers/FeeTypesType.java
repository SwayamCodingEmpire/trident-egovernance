package com.trident.egovernance.helpers;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum FeeTypesType {
    COMPULSORY_FEES("COMPULSORY FEES"),
    OPTIONAL_FEES("OPTIONAL FEES");
    private final String displayName;

    FeeTypesType(String displayName) {
        this.displayName = displayName;
    }
}