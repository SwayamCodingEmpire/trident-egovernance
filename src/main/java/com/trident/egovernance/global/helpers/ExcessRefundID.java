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
    private String voucherNo;;


    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        ExcessRefundID excessRefundID = (ExcessRefundID)o;
        return Objects.equals(regdNo,excessRefundID.regdNo) && Objects.equals(voucherNo,excessRefundID.voucherNo);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,voucherNo);
    }
}
