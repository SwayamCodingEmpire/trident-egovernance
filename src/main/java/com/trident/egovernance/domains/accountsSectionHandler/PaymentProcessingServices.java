package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.MoneyReceipt;
import com.trident.egovernance.dto.OtherFeesPayment;
import com.trident.egovernance.dto.PaymentProcessingInternalData;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.Student;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentProcessingServices {
    PaymentProcessingInternalData processPayment(FeeCollection feeCollection, Student student, String regdNo, List<DuesDetails> duesDetails, BigDecimal collectedFees, long slNo);
    MoneyReceipt processPaymentAutoMode(FeeCollection feeCollection, String regdNo, boolean isUpdate);
    MoneyReceipt processPaymentNonAutoModes(FeeCollection feeCollection, String regdNo, boolean isUpdate);

    MoneyReceipt processOtherFeesPayment(OtherFeesPayment otherFeesPayment, String regdNo, boolean isUpdate);
    MoneyReceipt updateFeesCollection(FeeCollection feeCollection);
    boolean deleteFeeCollection(Long mrNo);
}
