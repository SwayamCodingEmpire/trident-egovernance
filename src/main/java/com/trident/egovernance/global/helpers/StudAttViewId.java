package com.trident.egovernance.global.helpers;

import lombok.*;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StudAttViewId {
    private String regdNo;  //
    private String subCode;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        StudAttViewId studAttViewId = (StudAttViewId)o;
        return Objects.equals(regdNo,studAttViewId.regdNo) && Objects.equals(subCode,studAttViewId.subCode);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,subCode);
    }
}
