package com.trident.egovernance.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DuesDetailsId implements Serializable {
    private String regdNo;
    private String description;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        DuesDetailsId duesDetailsId = (DuesDetailsId)o;
        return Objects.equals(regdNo,duesDetailsId.regdNo) && Objects.equals(description,duesDetailsId.description);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,description);
    }
}
