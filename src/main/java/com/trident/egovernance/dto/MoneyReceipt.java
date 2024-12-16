package com.trident.egovernance.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MoneyReceipt {
    private String paymentDate;
    private Long mrNo;
    private List<MrDetailsDto> tat;
    private MoneyDTO tatTotalAmount;
    private List<MrDetailsDto> tactF;
    private MoneyDTO tactFTotalAmount;
    private FeeCollectionDetails feeCollectionDetails;
    private PaymentDuesDetails paymentDuesDetails;
}
