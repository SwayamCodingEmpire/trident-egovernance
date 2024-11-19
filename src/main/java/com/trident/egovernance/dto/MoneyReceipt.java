package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MoneyReceipt {
    private List<MrDetailsDto> tat;
    private List<MrDetailsDto> tactF;
    private FeeCollectionDetails feeCollectionDetails;
    private PaymentDuesDetails paymentDuesDetails;
}
