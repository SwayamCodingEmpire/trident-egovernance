package com.trident.egovernance.domains.nsrHandler.services;

import com.trident.egovernance.dto.DuesDetailsInitiationDTO;
import com.trident.egovernance.dto.StudentRequiredFieldsDTO;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.global.helpers.*;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.services.MasterTableServices;
import com.trident.egovernance.global.services.MiscellaniousServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DuesInitiationServiceImpl implements DuesInitiationService {
    private final DuesDetailsRepository duesDetailsRepository;
    private final MasterTableServices masterTableServicesImpl;
    private final Logger logger = LoggerFactory.getLogger(DuesInitiationServiceImpl.class);
    private final MiscellaniousServices miscellaniousServices;

    public DuesInitiationServiceImpl(DuesDetailsRepository duesDetailsRepository, MasterTableServices masterTableServicesImpl, MiscellaniousServices miscellaniousServices) {
        this.duesDetailsRepository = duesDetailsRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
        this.miscellaniousServices = miscellaniousServices;
    }

//    @Async("taskExecutor")

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Boolean initiateDuesDetails(DuesDetailsInitiationDTO student) {
            logger.info("Fetching fees from database");
            List<Fees> fees = masterTableServicesImpl.getFeesByBatchIdAndRegdYear(student.batchId(), student.currentYear());
//            isInterrupted(sharedState.isProceed());
            logger.info(fees.toString());

            Set<String> descriptions = fees.stream()
                    .map(Fees::getDescription)
                    .collect(Collectors.toCollection(TreeSet::new));

            Map<String, StandardDeductionFormat> deductionFormatMap = masterTableServicesImpl.getStandardDeductionformatByDescriptions(descriptions)
                    .stream()
                    .collect(Collectors.toMap(StandardDeductionFormat::getDescription, standardDeductionFormat -> standardDeductionFormat));

            Boolean plPool = BooleanString.YES.equals(student.plpoolm());
            Boolean indusTraining = BooleanString.YES.equals(student.indortrng());


            List<DuesDetails> duesDetailsList = fees.stream()
                    .filter(fee -> miscellaniousServices.isRelevantFee(
                            fee,
                            new StudentRequiredFieldsDTO(
                                    student.tfw(),
                                    student.transportOpted(),
                                    student.hostelOption()),
                            plPool,
                            indusTraining)
                    )
                    .map(fee -> createDuesDetails(fee, student, deductionFormatMap))
                    .filter(Objects::nonNull)
                    .toList();


            logger.info("Saving dues details to database");
//            isInterrupted(sharedState.isProceed());
            duesDetailsRepository.saveAllAndFlush(duesDetailsList);
//            isInterrupted(sharedState.isProceed());
            logger.info("Saved dues details to database");


            return true;

    }

    // Helper method to check if the fee is relevant


    // Helper method to create DuesDetails for each fee
    public DuesDetails createDuesDetails(Fees fee, DuesDetailsInitiationDTO student, Map<String, StandardDeductionFormat> deductionFormatMap) {
        try {
            int admissionYear = Year.now().getValue();
            DuesDetails duesDetails = new DuesDetails();
            duesDetails.setId(-1L);
            duesDetails.setRegdNo(student.regdNo());
            duesDetails.setDescription(fee.getDescription());
            duesDetails.setBalanceAmount(fee.getAmount());
            duesDetails.setAmountPaid(BigDecimal.ZERO);
            duesDetails.setAmountDue(fee.getAmount());
            duesDetails.setDeductionOrder(deductionFormatMap.get(fee.getDescription()).getDeductionOrder());
            duesDetails.setDueYear(student.currentYear());
            duesDetails.setSessionId(masterTableServicesImpl.getSessionId(student.course(), duesDetails.getDueYear(), 2024, student.studentType()));
            duesDetails.setAmountPaidToJee(BigDecimal.ZERO);
            duesDetails.setDueDate(Date.valueOf(LocalDate.now()));
            return duesDetails;
        } catch (Exception e) {
            logger.error("Error creating DuesDetails for fee: " + fee.getDescription() + " - " + e.getMessage());
            throw new RuntimeException("Error creating DuesDetails for fee: " + fee.getDescription() + " - " + e.getMessage());
        }
    }

    private void isInterrupted(boolean proceed) throws InterruptedException {
        if (!proceed) {
            throw new InterruptedException();
        }
    }

}
