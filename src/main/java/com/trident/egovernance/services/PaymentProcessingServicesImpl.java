package com.trident.egovernance.services;

import com.trident.egovernance.dto.MrDetailsDto;
import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.exceptions.InvalidStudentException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.MrHead;
import com.trident.egovernance.repositories.permanentDB.*;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentProcessingServicesImpl implements PaymentProcessingServices {
    private final StudentRepository studentRepository;
    private final CurrentSessionFetcherServices currentSessionFetchingService;
    private final MasterTableServices masterTableServicesImpl;
    private final DuesDetailsRepository duesDetailsRepository;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(PaymentProcessingServicesImpl.class);
    private final FeeCollectionRepository feeCollectionRepository;

    public PaymentProcessingServicesImpl(StudentRepository studentRepository, MasterTableServices masterTableServicesImpl, CurrentSessionFetcherServices currentSessionFetchingService, DuesDetailsRepository duesDetailsRepository, EntityManager entityManager, FeeCollectionRepository feeCollectionRepository) {
        this.currentSessionFetchingService = currentSessionFetchingService;
        this.studentRepository = studentRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
        this.duesDetailsRepository = duesDetailsRepository;
        this.entityManager = entityManager;
        this.feeCollectionRepository = feeCollectionRepository;
    }
    @Transactional
    @Override
    public MrDetailsSorted processPayment(FeeCollection feeCollection,String regdNo){
        if(!studentRepository.existsById(regdNo)){
            throw new InvalidStudentException("Invalid Registration Number");
        }
        else if(feeCollection.getCollectedFee().compareTo(BigDecimal.ZERO)<=0){
            throw new InvalidInputsException("Invalid Collected Fee");
        }
        BigDecimal collectedFees = feeCollection.getCollectedFee();
        Student student = entityManager.getReference(Student.class,regdNo);
        feeCollection.setPaymentDate(String.valueOf(Year.now().getValue()));
        feeCollection.setStudent(student);
        List<MrDetails> mrDetailsList = new ArrayList<>();
        List<DuesDetails> duesDetails = duesDetailsRepository.findAllByRegdNoAndBalanceAmountNotOrderByDeductionOrder(regdNo,BigDecimal.ZERO);
        logger.info("Dues details: ");
        logger.info(duesDetails.toString());
        long slNo = 1;
        for (DuesDetails duesDetail : duesDetails) {
            feeCollection.setDueYear(duesDetail.getDueYear());
            if(collectedFees.compareTo(BigDecimal.ZERO)>0) {
                MrDetails mrDetails = createMrDetails(duesDetail,collectedFees,slNo);
                collectedFees = updateDuesAndCollectedFees(duesDetail,collectedFees);
                mrDetails.setFeeCollection(feeCollection);
                mrDetailsList.add(mrDetails);
            }
            else if(collectedFees.compareTo(BigDecimal.ZERO)==0){
                logger.info("Collected fees reached 0");
                break;
            }
            slNo++;
        }
        if(collectedFees.compareTo(BigDecimal.ZERO)>0){
            DuesDetails duesDetail = new DuesDetails();
            duesDetail.setRegdNo(regdNo);
            duesDetail.setAmountDue(BigDecimal.ZERO);
            duesDetail.setAmountPaid(collectedFees);
            duesDetail.setBalanceAmount(duesDetail.getAmountDue().subtract(duesDetail.getAmountPaid()));
            duesDetail.setDescription("EXCESS FEE");
            duesDetail.setDueDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
            duesDetail.setDueYear(feeCollection.getDueYear());
            duesDetail.setSessionId(feeCollection.getSessionId());
            duesDetail.setDeductionOrder(masterTableServicesImpl.getStandardDeductionFormat("EXCESS FEE").orElseThrow(()-> new RecordNotFoundException("Invalid Input")).getDeductionOrder());
            duesDetails.add(duesDetail);
        }
        logger.info(duesDetails.toString());
        duesDetailsRepository.saveAllAndFlush(duesDetails);
        String session = currentSessionFetchingService.fetchCurrentSessionForStudent(regdNo);
        feeCollection.setSessionId(session);
        feeCollection.setMrDetails(mrDetailsList);
        FeeCollection savedFeeCollection = feeCollectionRepository.save(feeCollection);
        return sortMrDetailsByMrHead(feeCollection.getMrDetails().stream()
                .map(mrDetail -> new MrDetailsDto(savedFeeCollection.getMrNo(),mrDetail.getId(),mrDetail.getSlNo(),mrDetail.getParticulars(),mrDetail.getAmount()))
                .collect(Collectors.toList()));
    }

    private MrDetailsSorted sortMrDetailsByMrHead(List<MrDetailsDto> mrDetailsDtos){
        List<String> descriptionsOfMrHead = mrDetailsDtos.stream()
                        .map(MrDetailsDto::getParticulars)
                                .collect(Collectors.toList());
        HashMap<String, MrHead> feeTypesMrHeadHashMap = masterTableServicesImpl.convertFeeTypesMrHeadToHashMap(descriptionsOfMrHead);
        MrDetailsSorted mrDetailsSorted = new MrDetailsSorted();

        List<MrDetailsDto> tat = new ArrayList<>();
        List<MrDetailsDto> tactF = new ArrayList<>();
        for(MrDetailsDto mrDetailsDto: mrDetailsDtos){
            if(feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TAT)){
                tat.add(mrDetailsDto);
            }
            else if(feeTypesMrHeadHashMap.get(mrDetailsDto.getParticulars()).equals(MrHead.TACTF)){
                tactF.add(mrDetailsDto);
            }
        }
        mrDetailsSorted.setTat(tat);
        mrDetailsSorted.setTactF(tactF);
        return mrDetailsSorted;
    }

    private MrDetails createMrDetails(DuesDetails duesDetails, BigDecimal collectedFees, long slNo){
        MrDetails mrDetails = new MrDetails();
        mrDetails.setSlNo(slNo);
        if(duesDetails.getDescription().compareTo("EXCESS FEE")==0){
            mrDetails.setAmount((duesDetails.getBalanceAmount().subtract(collectedFees)).multiply(BigDecimal.valueOf(-1)));
        }
        else if(collectedFees.compareTo(duesDetails.getBalanceAmount())<0){
            mrDetails.setAmount(collectedFees);
        }
        else {
            mrDetails.setAmount(duesDetails.getBalanceAmount());
        }
        mrDetails.setParticulars(duesDetails.getDescription());
        return mrDetails;
    }

    private BigDecimal updateDuesAndCollectedFees(DuesDetails duesDetail, BigDecimal collectedFees){
        if(duesDetail.getDescription().compareTo("EXCESS FEE")==0){
            duesDetail.setAmountPaid(duesDetail.getAmountPaid().add(collectedFees));
            duesDetail.setBalanceAmount(duesDetail.getAmountDue().subtract(duesDetail.getAmountPaid()));
            return BigDecimal.ZERO;
        }
        else if(collectedFees.compareTo(duesDetail.getBalanceAmount())<0){
            logger.info("Collected fees: "+collectedFees);
            logger.info("Dues detail balance amount: "+duesDetail.getBalanceAmount());
            duesDetail.setBalanceAmount(duesDetail.getBalanceAmount().subtract(collectedFees));
            duesDetail.setAmountPaid(collectedFees);
            return BigDecimal.ZERO;
        }
        else {
            logger.info("Collected fees: "+collectedFees);
            logger.info("Dues detail balance amount: "+duesDetail.getBalanceAmount());
            BigDecimal remaining = collectedFees.subtract(duesDetail.getBalanceAmount());
            logger.info("Remaining amount: "+remaining);
            duesDetail.setAmountPaid(duesDetail.getBalanceAmount());
            duesDetail.setBalanceAmount(BigDecimal.ZERO);
            return remaining;
        }
    }
}
