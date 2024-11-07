package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.dto.StudentRequiredFieldsDTO;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.helpers.*;
import com.trident.egovernance.global.repositories.permanentDB.FeeTypesRepository;
import com.trident.egovernance.global.repositories.permanentDB.FeesRepository;
import com.trident.egovernance.global.repositories.permanentDB.SessionsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StandardDeductionFormatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MasterTableServicesImpl implements MasterTableServices {
    private final Logger logger = LoggerFactory.getLogger(MasterTableServicesImpl.class);
    private final FeesRepository feesRepository;
    private final SessionsRepository sessionsRepository;
    private final FeeTypesRepository feeTypesRepository;
    private final StandardDeductionFormatRepository standardDeductionFormatRepository;

    public MasterTableServicesImpl(FeesRepository feesRepository, SessionsRepository sessionsRepository, FeeTypesRepository feeTypesRepository, StandardDeductionFormatRepository standardDeductionFormatRepository) {
        this.feesRepository = feesRepository;
        this.sessionsRepository = sessionsRepository;
        this.feeTypesRepository = feeTypesRepository;
        this.standardDeductionFormatRepository = standardDeductionFormatRepository;
    }
    @Cacheable(value = "fees", key = "#batchId")
    @Override
    public List<Fees> getFeesByBatchIdAndRegdYear(String batchId,Integer regdYear){
        logger.info("Fetching fees by batchId: {}", batchId);
        try {
            List<Fees> fees = feesRepository.findAllByBatchIdAndRegdYear(batchId,regdYear);
            logger.info("Fetched fees by batchId: {}", fees);
            return fees;
        } catch (Exception e) {
            logger.error("Error while fetching fees by batchId", e);
            return Collections.emptyList(); // Or handle appropriately
        }
    }



    @Cacheable(value = "standardDeductionFormat", key = "#descriptions.toString()")
    @Override
    public List<StandardDeductionFormat> getStandardDeductionformatByDescriptions(Set<String> descriptions){
        logger.info("Fetching standardDeductionFormat by descriptions: {}", descriptions);
        return standardDeductionFormatRepository.findByDescriptions(descriptions);
    }

    @Cacheable(value = "SessionId", key = "#course+#regdYear+#admissionYear+#studentType")
    @Override
    public String getSessionId(String course, int regdYear, int admissionYear, String studentType){
//        String currYear = String.valueOf(Year.now().getValue());
//        String nextYear = String.valueOf(Year.now().getValue()+1);
//        return currYear+"-"+nextYear;
        SessionIdId sessionIdId = new SessionIdId(course,regdYear, admissionYear,studentType);
        logger.info(sessionIdId.toString());
        Sessions sessions = sessionsRepository.findById(sessionIdId).orElseThrow(() -> new RecordNotFoundException("Session not found"));
        logger.info(sessions.toString());
        logger.info(sessions.toString());
        return sessions.getSessionId();
    }

    @Cacheable(value = "singularStandardDeductionFormat", key = "#description")
    @Override
    public Optional<StandardDeductionFormat> getStandardDeductionFormat(String description) {
        return standardDeductionFormatRepository.findById(description);
    }

    @Override
    public List<FeeTypesMrHead> getFeeTypesMrHeadByDescriptions(List<String> descriptions){
        return feeTypesRepository.findByDescriptionIn(descriptions);
    }

    @Override
    public HashMap<String, MrHead> convertFeeTypesMrHeadToHashMap(List<String> descriptions){
        List<FeeTypesMrHead> feeTypesMrHeads = feeTypesRepository.findByDescriptionIn(descriptions);
        HashMap<String, MrHead> feeTypesMrHeadHashMap = new HashMap<>();
        for (FeeTypesMrHead feeTypesMrHead : feeTypesMrHeads) {
            feeTypesMrHeadHashMap.put(feeTypesMrHead.description(), feeTypesMrHead.mrHead());
        }
        return feeTypesMrHeadHashMap;
    }

    // Helper method to check if the fee is compulsory
    @Override
    public boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining) {
        return FeeTypesType.COMPULSORY_FEES.equals(FeeTypesType.fromDisplayName(fee.getFeeType().getType().getDisplayName())) &&
                ((fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && indusTraining) ||
                        (fee.getDescription().equals("PRE PLACEMENT TRAINING FEE") && plPool) ||
                        (!fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && !fee.getDescription().equals("PRE PLACEMENT TRAINING FEE")));
    }

    @Override
    public boolean isRelevantFee(Fees fee, StudentRequiredFieldsDTO student, Boolean plPool, Boolean indusTraining) {
        return (fee.getTfwType().equals(student.tfw()) || fee.getTfwType().equals(TFWType.ALL)) &&
                (isCompulsoryFee(fee, plPool, indusTraining) ||
                        (BooleanString.YES.equals(student.transportOpted()) && "TRANSPORTFEE".equals(fee.getFeeType().getFeeGroup())) ||
                        (BooleanString.YES.equals(student.hostelOption()) && "HOSTELFEE".equals(fee.getFeeType().getFeeGroup())));
    }

    @Override
    public FeeTypes getFeeTypesByFeeGroupAndSemester(String feeGroup, Integer semester) {
        return feeTypesRepository.findByFeeGroupAndSemester(feeGroup, semester);
    }
}