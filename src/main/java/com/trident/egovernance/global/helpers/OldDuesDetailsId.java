package com.trident.egovernance.global.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class OldDuesDetailsId {

    private Long id;
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
        OldDuesDetailsId oldDuesDetails = (OldDuesDetailsId)o;
        return Objects.equals(regdNo,oldDuesDetails.regdNo) && Objects.equals(description,oldDuesDetails.description) && Objects.equals(id,oldDuesDetails.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,description,id);
    }
}
