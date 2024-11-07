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
public class NotPromotedId {
    private String regdNo;
    private Integer currentYear;
    private String sessionId;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        NotPromotedId notPromotedId = (NotPromotedId) o;
        return Objects.equals(regdNo,notPromotedId.regdNo) && Objects.equals(currentYear,notPromotedId.currentYear) && Objects.equals(sessionId,notPromotedId.sessionId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,currentYear,sessionId);
    }
}
