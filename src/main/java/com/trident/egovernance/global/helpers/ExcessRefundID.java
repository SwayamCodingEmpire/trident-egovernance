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

public class ExcessRefundID {
    private String regdNo;
    private String voucherNo;
    private CollegeName collegeName;


    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        ExcessRefundID excessRefundID = (ExcessRefundID)o;
        return Objects.equals(regdNo,excessRefundID.regdNo) && Objects.equals(voucherNo,excessRefundID.voucherNo) && Objects.equals(collegeName,excessRefundID.collegeName);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,voucherNo,collegeName);
    }
}
