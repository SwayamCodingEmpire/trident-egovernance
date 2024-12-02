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
    private List<MrDetailsDto> tactF;
    private FeeCollectionDetails feeCollectionDetails;
    private PaymentDuesDetails paymentDuesDetails;
}
