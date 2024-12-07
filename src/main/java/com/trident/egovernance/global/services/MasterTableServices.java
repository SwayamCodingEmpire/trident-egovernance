package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.MrHead;
import com.trident.egovernance.global.helpers.StudentType;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MasterTableServices {
    List<Fees> getFeesByBatchIdAndRegdYear(String batchId, Integer regdYear);
    List<StandardDeductionFormat> getStandardDeductionformatByDescriptions(Set<String> descriptions);
    String getSessionId(Courses course, int regdYear, int admissionYear, StudentType studentType);
    int getAdmissionYearFromSession(String sessionId, Courses course, int regdYear, StudentType studentType);
    boolean endSession(Date endDate, String sessionId, Courses course, int regdYear, StudentType studentType);
    Optional<StandardDeductionFormat> getStandardDeductionFormat(String description);
    List<FeeTypesMrHead> getFeeTypesMrHeadByDescriptions(List<String> descriptions);
    HashMap<String, MrHead> convertFeeTypesMrHeadToHashMap(List<String> descriptions);
    boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining);
    boolean isRelevantFee(Fees fee, StudentRequiredFieldsDTO student, Boolean plPool, Boolean indusTraining);
    Set<String> getAllOtherFeesDescriptions();
    FeeTypes getFeeTypesByFeeGroupAndSemester(String feeGroup, Integer semester);
    Set<String> getAllSessions();
    Set<String> getAllParticulars();
    List<PaymentMode> getAllPaymentModes();
    List<FeeTypesOnly> getAllFeeTypesForFeeAddition(Set<FeeTypesType> feeTypesTypes);
    List<Fees> saveFeesToDatabase(FeesCRUDDto feesCRUDDto);
    Set<FeesOnly> getFeesByBatchId(BasicFeeBatchDetails basicFeeBatchDetails);
    List<Fees> updateFees(FeesCRUDDto feesCRUDDto);
    Set<FeeTypesOnly> createNewFeeTypes(Set<FeeTypesOnly> feeTypes);
}
