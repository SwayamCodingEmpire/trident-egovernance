package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.FeeCollectionDetails;
import com.trident.egovernance.dto.MoneyReceipt;
import com.trident.egovernance.dto.MrDetailsDto;
import com.trident.egovernance.dto.PaymentDuesDetails;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;

import java.util.List;

public interface FeeCollectionTransactionServices {
    MoneyReceipt getMrDetailsSorted(FeeCollection processedFeeCollection, List<DuesDetails> processedDuesDetails);
    MoneyReceipt sortMrDetailsByMrHead(List<MrDetailsDto> mrDetailsDtos, FeeCollectionDetails feeCollectionDetails, PaymentDuesDetails paymentDuesDetails);
    MoneyReceipt getMoneyReceiptByMrNo(Long mrNo);
    int deleteFeeCollectionRecord(FeeCollection oldFeeCollection);
}
