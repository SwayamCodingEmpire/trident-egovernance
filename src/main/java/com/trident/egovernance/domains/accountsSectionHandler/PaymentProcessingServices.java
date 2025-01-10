package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.MoneyReceipt;
import com.trident.egovernance.dto.OtherFeesPayment;
import com.trident.egovernance.dto.PaymentProcessingInternalData;
import com.trident.egovernance.dto.StudentBasicDTO;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.Student;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentProcessingServices {
    PaymentProcessingInternalData processPayment(FeeCollection feeCollection, Student student, String regdNo, List<DuesDetails> duesDetails, BigDecimal collectedFees, long slNo);
    Pair<MoneyReceipt, StudentBasicDTO> processPaymentAutoMode(FeeCollection feeCollection, String regdNo, boolean isUpdate);
    Pair<MoneyReceipt,StudentBasicDTO> processPaymentNonAutoModes(FeeCollection feeCollection, String regdNo, boolean isUpdate);

    MoneyReceipt processPaymentInterface(FeeCollection feeCollection, String regdNo, boolean isUpdate);
    Pair<MoneyReceipt,StudentBasicDTO> processOtherFeesPayment(OtherFeesPayment otherFeesPayment, String regdNo, boolean isUpdate, String paymentReceiver);
    MoneyReceipt processOtherFessPaymentInterface(OtherFeesPayment otherFeesPayment, String regdNo, boolean isUpdate);
//    MoneyReceipt updateFeesCollection(FeeCollection feeCollection);
    boolean deleteFeeCollection(Long mrNo);
}
