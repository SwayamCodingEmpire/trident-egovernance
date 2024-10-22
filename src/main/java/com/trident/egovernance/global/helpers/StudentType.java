package com.trident.egovernance.global.helpers;

import java.io.Serializable;

public enum StudentType implements Serializable {
    REGULAR,
    LE;
    public String getEnumName(){
        if(this.equals(REGULAR)){
            return "REGULAR";
        }
        else{
            return "LE";
        }
    }
}
