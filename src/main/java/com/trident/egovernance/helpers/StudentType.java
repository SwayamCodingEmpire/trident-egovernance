package com.trident.egovernance.helpers;

public enum StudentType {
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
