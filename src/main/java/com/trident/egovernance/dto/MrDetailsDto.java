package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MrDetailsDto {
    private Long mrNo;
    private Long id;
    private long slNo;
    private String particulars;
    private BigDecimal amount;

    public MrDetailsDto(MrDetails mrDetails){
        this.mrNo = mrDetails.getFeeCollection().getMrNo();
        this.id = mrDetails.getId();
        this.slNo = mrDetails.getSlNo();
        this.particulars = mrDetails.getParticulars();
        this.amount = mrDetails.getAmount();
    }
}
