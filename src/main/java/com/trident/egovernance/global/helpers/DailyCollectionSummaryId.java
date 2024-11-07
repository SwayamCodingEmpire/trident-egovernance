package com.trident.egovernance.global.helpers;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class DailyCollectionSummaryId {

    private String paymentDate;
    private String paymentMode;
    private String particulars;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        DailyCollectionSummaryId dailyCollectionSummaryId = (DailyCollectionSummaryId)o;
        return Objects.equals(paymentDate,dailyCollectionSummaryId.paymentDate) && Objects.equals(paymentMode,dailyCollectionSummaryId.paymentMode) && Objects.equals(particulars,dailyCollectionSummaryId.particulars);
    }

    @Override
    public int hashCode(){
        return Objects.hash(paymentDate,paymentMode,particulars);
    }
}
