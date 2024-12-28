package com.trident.egovernance.global.helpers;

import java.io.Serializable;

public enum StudentType implements Serializable {
    REGULAR,
    LE,
    NA;
    public String getEnumName(){
        if(this.equals(REGULAR)){
            return "REGULAR";
        }
        else if(this.equals(LE)){
            return "LE";
        }
        else {
            return "NA";
        }
    }
}
