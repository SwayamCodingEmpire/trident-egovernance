package com.trident.egovernance.domains.nsrHandler.services;

import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.helpers.*;
import com.trident.egovernance.domains.nsrHandler.DuesInitiationService;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.SessionsRepository;
import com.trident.egovernance.global.services.MasterTableServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
class DuesInitiationServiceImpl implements DuesInitiationService {
    private final DuesDetailsRepository duesDetailsRepository;
    private final SessionsRepository sessionsRepository;
    private final MasterTableServices masterTableServicesImpl;
    private final Logger logger = LoggerFactory.getLogger(DuesInitiationServiceImpl.class);

    public DuesInitiationServiceImpl(DuesDetailsRepository duesDetailsRepository, SessionsRepository sessionsRepository, MasterTableServices masterTableServicesImpl) {
        this.duesDetailsRepository = duesDetailsRepository;
        this.sessionsRepository = sessionsRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
    }


    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> initiateDuesDetails(NSR student, SharedStateAmongDueInitiationAndNSRService sharedState) {
        try {
            logger.info("Fetching fees from database");
            List<Fees> fees = masterTableServicesImpl.getFeesByBatchId(student.getBatchId());
            isInterrupted(sharedState.isProceed());

            Set<String> descriptions = fees.stream()
                    .map(Fees::getDescription)
                    .collect(Collectors.toCollection(TreeSet::new));

            Map<String, StandardDeductionFormat> deductionFormatMap = masterTableServicesImpl.getStandardDeductionformatByDescriptions(descriptions)
                    .stream()
                    .collect(Collectors.toMap(StandardDeductionFormat::getDescription, standardDeductionFormat -> standardDeductionFormat));

            Boolean plPool = BooleanString.YES.equals(student.getPlpoolm());
            Boolean indusTraining = BooleanString.YES.equals(student.getIndortrng());


                List<DuesDetails> duesDetailsList = fees.stream()
                        .filter(fee -> isRelevantFee(fee, student, plPool, indusTraining))
                        .map(fee ->  createDuesDetails(fee, student, deductionFormatMap))
                        .filter(Objects::nonNull)
                        .toList();

                logger.info("Saving dues details to database");
                isInterrupted(sharedState.isProceed());
                duesDetailsRepository.saveAllAndFlush(duesDetailsList);
                isInterrupted(sharedState.isProceed());
                logger.info("Saved dues details to database");


            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("Error occurred while processing dues details: " + e.getMessage());
            throw new RuntimeException("Error occurred while processing dues details: " + e.getMessage());
        }
    }

    // Helper method to check if the fee is relevant
    private boolean isRelevantFee(Fees fee, NSR student, Boolean plPool, Boolean indusTraining) {
        return (fee.getTfwType().equals(student.getTfw()) || fee.getTfwType().equals(TFWType.ALL)) &&
                (isCompulsoryFee(fee, plPool, indusTraining) ||
                        (BooleanString.YES.equals(student.getTransportOpted()) && "TRANSPORTFEE".equals(fee.getFeeType().getFeeGroup())) ||
                        (BooleanString.YES.equals(student.getHostelOption()) && "HOSTELFEE".equals(fee.getFeeType().getFeeGroup())));
    }

    // Helper method to check if the fee is compulsory
    private boolean isCompulsoryFee(Fees fee, Boolean plPool, Boolean indusTraining) {
        return FeeTypesType.COMPULSORY_FEES.equals(FeeTypesType.fromDisplayName(fee.getFeeType().getType().getDisplayName())) &&
                ((fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && indusTraining) ||
                        (fee.getDescription().equals("PRE PLACEMENT TRAINING FEE") && plPool) ||
                        (!fee.getDescription().equals("INDUSTRY-READY TRAINING FEE") && !fee.getDescription().equals("PRE PLACEMENT TRAINING FEE")));
    }

    // Helper method to create DuesDetails for each fee
    private DuesDetails createDuesDetails(Fees fee, NSR student, Map<String, StandardDeductionFormat> deductionFormatMap) {
        try {
            DuesDetails duesDetails = new DuesDetails();
            duesDetails.setRegdNo(student.getRegdNo());
            duesDetails.setDescription(fee.getDescription());
            duesDetails.setBalanceAmount(fee.getAmount());
            duesDetails.setAmountPaid(BigDecimal.ZERO);
            duesDetails.setAmountDue(fee.getAmount());
            duesDetails.setDeductionOrder(deductionFormatMap.get(fee.getDescription()).getDeductionOrder());
            duesDetails.setDueYear(student.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
            duesDetails.setSessionId(masterTableServicesImpl.getSessionId(student.getCourse().getDisplayName(), duesDetails.getDueYear(), Year.now().getValue(), student.getStudentType().getEnumName()));
            duesDetails.setAmountPaidToJee(BigDecimal.ZERO);
            duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
            return duesDetails;
        } catch (Exception e) {
            logger.error("Error creating DuesDetails for fee: " + fee.getDescription() + " - " + e.getMessage());
            throw new RuntimeException("Error creating DuesDetails for fee: " + fee.getDescription() + " - " + e.getMessage());
        }
    }

    private void isInterrupted(boolean proceed) throws InterruptedException {
        if(!proceed){
            throw new InterruptedException();
        }
    }

}
