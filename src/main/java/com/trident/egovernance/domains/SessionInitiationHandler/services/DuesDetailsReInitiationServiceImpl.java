package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.nsrHandler.services.DuesInitiationServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Fees;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import com.trident.egovernance.global.entities.permanentDB.StandardDeductionFormat;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.services.MasterTableServices;
import com.trident.egovernance.global.services.MiscellaniousServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DuesDetailsReInitiationServiceImpl {
    private final MasterTableServices masterTableServicesImpl;
    private final Logger logger = LoggerFactory.getLogger(DuesDetailsReInitiationServiceImpl.class);
    private final MiscellaniousServices miscellaniousServices;
    private final DuesInitiationServiceImpl duesInitiationServiceImpl;
    private final DuesDetailsRepository duesDetailsRepository;

    public DuesDetailsReInitiationServiceImpl(MasterTableServices masterTableServices, MiscellaniousServices miscellaniousServices, DuesInitiationServiceImpl duesInitiationServiceImpl, DuesDetailsRepository duesDetailsRepository) {
        this.masterTableServicesImpl = masterTableServices;
        this.miscellaniousServices = miscellaniousServices;
        this.duesInitiationServiceImpl = duesInitiationServiceImpl;
        this.duesDetailsRepository = duesDetailsRepository;
    }

    public boolean reInitiateDuesDetails(List<DuesDetailsInitiationDTO> students, Set<FeeCollectionDTOWithRegdNo> feeCollection) {
        try {
            logger.info("Starting re-initiation of dues details for students");
            List<DuesDetails> allDuesDetails = new ArrayList<>();

            List<Fees> fees = masterTableServicesImpl.getFeesByBatchIdAndRegdYear(students.getFirst().batchId(), students.getFirst().currentYear());
            logger.info("Fees fetched for student {}: {}", students.getFirst().regdNo(), fees);

            Set<String> descriptions = fees.stream()
                    .map(Fees::getDescription)
                    .collect(Collectors.toCollection(TreeSet::new));

            Map<String, StandardDeductionFormat> deductionFormatMap = masterTableServicesImpl.getStandardDeductionformatByDescriptions(descriptions)
                    .stream()
                    .collect(Collectors.toMap(StandardDeductionFormat::getDescription, standardDeductionFormat -> standardDeductionFormat));


            for (DuesDetailsInitiationDTO student : students) {
                logger.info("Processing student with registration number: {}", student.regdNo());

                // Fetch fees for the student's batch and current year


                Boolean plPool = BooleanString.YES.equals(student.plpoolm());
                Boolean indusTraining = BooleanString.YES.equals(student.indortrng());

                // Create dues details for the student
                List<DuesDetails> studentDuesDetails = fees.stream()
                        .filter(fee -> miscellaniousServices.isRelevantFee(
                                fee,
                                new StudentRequiredFieldsDTO(
                                        student.tfw(),
                                        student.transportOpted(),
                                        student.hostelOption()),
                                plPool,
                                indusTraining)
                        )
                        .map(fee -> duesInitiationServiceImpl.createDuesDetails(fee, student, deductionFormatMap))
                        .filter(Objects::nonNull)
                        .toList();

                // Add the student's dues details to the overall list
                allDuesDetails.addAll(studentDuesDetails);
                logger.info("Processed dues details for student {}", student.regdNo());
                // Check for interruption between students
            }

            // Create a map of dues details with regdNo and description as key
            Map<String, DuesDetails> duesDetailsMap = allDuesDetails.stream()
                    .collect(Collectors.toMap(
                            dues -> dues.getRegdNo() + "::" + dues.getDescription(),
                            dues -> dues
                    ));

            // Update dues details based on fee collection
            for (FeeCollectionDTOWithRegdNo fee : feeCollection) {
                String key = fee.regdNo() + "::" + fee.feeCollection().getMrDetails().stream().findFirst()
                        .map(MrDetails::getParticulars)
                        .orElse(null);

                if (key != null && duesDetailsMap.containsKey(key)) {
                    DuesDetails duesDetails = duesDetailsMap.get(key);
                    BigDecimal collectedFee = fee.feeCollection().getCollectedFee();
                    duesDetails.setAmountPaid(duesDetails.getAmountPaid().add(collectedFee));
                    duesDetails.setBalanceAmount(duesDetails.getBalanceAmount().subtract(collectedFee));
                    logger.info("Updated dues for regdNo: {}, description: {}", fee.regdNo(), key);
                }
            }
            // Save all dues details in bulk
            logger.info("Saving all dues details to database");
            duesDetailsRepository.saveAllAndFlush(allDuesDetails);
            logger.info("Saved all dues details to database");

            return true;
        } catch (Exception e) {
            logger.error("Error occurred while processing dues details for multiple students: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while processing dues details: " + e.getMessage());
        }
    }
}
