package com.trident.egovernance.services;

import com.trident.egovernance.dto.FeeTypesMrHead;
import com.trident.egovernance.dto.MrDetailsDto;
import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.entities.permanentDB.*;
import com.trident.egovernance.exceptions.InvalidStudentException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.helpers.MrHead;
import com.trident.egovernance.repositories.permanentDB.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
public class PaymentProcessingServiceImpl {
    private final DiscountRepository discountRepository;
    private final StudentRepository studentRepository;
    private final MasterTableServices masterTableServices;
    private final MrDetailsRepository mrDetailsRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(PaymentProcessingServiceImpl.class);
    private final FeeCollectionRepository feeCollectionRepository;

    public PaymentProcessingServiceImpl(DiscountRepository discountRepository, StudentRepository studentRepository, MasterTableServices masterTableServices, MrDetailsRepository mrDetailsRepository, DuesDetailsRepository duesDetailsRepository, EntityManager entityManager,
                                        FeeCollectionRepository feeCollectionRepository) {
        this.discountRepository = discountRepository;
        this.studentRepository = studentRepository;
        this.masterTableServices = masterTableServices;
        this.mrDetailsRepository = mrDetailsRepository;
        this.duesDetailsRepository = duesDetailsRepository;
        this.entityManager = entityManager;
        this.feeCollectionRepository = feeCollectionRepository;
    }

//    @Transactional
//    public void processPayment(FeeCollection feeCollection,String regdNo){
//        if(!studentRepository.existsById(regdNo)){
//            throw new InvalidStudentException("Invalid Registration Number");
//        }
//        BigDecimal collectedFees = feeCollection.getCollectedFee();
//        Student student = entityManager.getReference(Student.class,regdNo);
//        feeCollection.setPaymentDate(String.valueOf(Year.now().getValue()));
//        feeCollection.setStudent(student);
//        long mrNo = feeCollectionRepository.getMaxMrNo()+1;
////        feeCollection.setMrNo(mrNo);
//        List<MrDetails> mrDetailsList = new ArrayList<>();
//        List<DuesDetails> duesDetails = duesDetailsRepository.findAllByRegdNoOrderByDeductionOrder(regdNo);
//        long id = mrDetailsRepository.getMaxId();
//        long slNo = 1;
//        for (DuesDetails duesDetail : duesDetails) {
//            feeCollection.setDueYear(duesDetail.getDueYear());
//            MrDetails mrDetails1 = new MrDetails();
//            if(collectedFees.compareTo(BigDecimal.ZERO)>0) {
//                switch (collectedFees.compareTo(duesDetail.getAmountDue())) {
//                    case -1: // collectedFees < duesDetail.getAmountDue()
//                        logger.info("Dues detail amount due: "+duesDetail.getAmountDue());
//                        duesDetail.setAmountDue(duesDetail.getAmountDue().subtract(collectedFees));
//                        duesDetail.setAmountPaid(collectedFees);
//                        logger.info("Dues detail amount due after subtraction: "+duesDetail.getAmountDue());
//                        mrDetails1.setSlNo(slNo);
//                        mrDetails1.setId(id + 1);
//                        mrDetails1.setAmount(collectedFees);
//                        collectedFees = BigDecimal.ZERO;
//                        break;
//                    case 0: // collectedFees == duesDetail.getAmountDue()
//                        logger.info("Dues detail amount due: "+duesDetail.getAmountDue());
//                        duesDetail.setAmountDue(BigDecimal.ZERO);
//                        logger.info("Dues detail amount due after subtraction: "+duesDetail.getAmountDue());
//                        mrDetails1.setSlNo(slNo);
//                        mrDetails1.setId(id + 1);
//                        mrDetails1.setAmount(collectedFees);
//                        duesDetail.setAmountPaid(collectedFees);
//                        collectedFees = BigDecimal.ZERO;
//                        break;
//                    case 1: // collectedFees > duesDetail.getAmountDue()
//                        duesDetail.setAmountPaid(duesDetail.getAmountDue());
//                        logger.info("Dues detail amount due: "+duesDetail.getAmountDue());
//                        duesDetail.setAmountDue(BigDecimal.ZERO);
//                        logger.info("Dues detail amount due after subtraction: "+duesDetail.getAmountDue());
//                        mrDetails1.setSlNo(slNo);
//                        mrDetails1.setId(id + 1);
//                        mrDetails1.setAmount(collectedFees);
//                        collectedFees = collectedFees.subtract(duesDetail.getAmountDue());
//                        break;
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + collectedFees.compareTo(duesDetail.getAmountDue()));
//                }
//                mrDetails1.setParticulars(duesDetail.getDescription());
//                mrDetails1.setFeeCollection(feeCollection);
//
////                mrDetails1.setMrNo(mrNo);
//                // Set particulars and add to the list
//                mrDetailsList.add(mrDetails1);
//            }
//            else if(collectedFees.compareTo(BigDecimal.ZERO)==0){
//                logger.info("Collected fees is 0");
//                mrDetailsRepository.saveAllAndFlush(mrDetailsList);
//                break;
//            }
//            id++;
//            slNo++;
//        }
//        if(collectedFees.compareTo(BigDecimal.ZERO)>0){
//            logger.info(collectedFees.toString());
//            throw new InvalidStudentException("Amount paid is greater than dues");
//        }
//        logger.info(duesDetails.toString());
//        duesDetailsRepository.saveAllAndFlush(duesDetails);
//        String sql = "SELECT SESSIONID " +
//                "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo";
//        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter("regdNo",regdNo);
//        Object result = query.getSingleResult();
//        String session;
//        if(result == null){
//            logger.info("No current session found for student");
//            throw new InvalidStudentException("No current session found for student");
//        }
//        else{
//            session = result.toString();
//        }
//        logger.info(mrNo+" "+session);
//        feeCollection.setMrNo(mrNo);
//        feeCollection.setSessionId(session);
//        feeCollection.setMrDetails(mrDetailsList);
////        feeCollection.setSessionId(masterTableServices.getSessionId());
//        feeCollectionRepository.save(feeCollection);
//        logger.info(feeCollectionRepository.getMaxMrNo().toString());
//    }

//    public String getCurrentSessionForStudent(String regdNo){
//
//    }
    @Transactional
    public MrDetailsSorted processPayment(FeeCollection feeCollection,String regdNo){
        if(!studentRepository.existsById(regdNo)){
            throw new InvalidStudentException("Invalid Registration Number");
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
            duesDetail.setDeductionOrder(masterTableServices.getStandardDeductionFormat("EXCESS FEE").orElseThrow(()-> new RecordNotFoundException("Invalid Input")).getDeductionOrder());
            duesDetails.add(duesDetail);
        }
        logger.info(duesDetails.toString());
        duesDetailsRepository.saveAllAndFlush(duesDetails);
        String session = fetchCurrentSessionForStudent(regdNo);
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
        HashMap<String, MrHead> feeTypesMrHeadHashMap = masterTableServices.convertFeeTypesMrHeadToHashMap(descriptionsOfMrHead);
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


    private String fetchCurrentSessionForStudent(String regdNo){
        String sql = "SELECT SESSIONID " +
                "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("regdNo",regdNo);
        Object result = query.getSingleResult();
        String session;
        if(result == null){
            logger.info("No current session found for student");
            throw new InvalidStudentException("No current session found for student");
        }
        else{
            session = result.toString();
        }
        return session;
    }

    @Transactional
    public Boolean insertDiscountData(Discount discount) {
        if(discount.getRegdNo().compareTo("ALL")==0){
            String sql = "SELECT REGDNO " +
                    "FROM FEECDEMO.CURRENT_SESSION WHERE CURRENTYEAR = :currentYear";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("currentYear", discount.getCurrentYear());
            List<String> regdNos = query.getResultList();
            if (regdNos.isEmpty()) {
                logger.info("No students found for the current year");
                return false;
            } else {
                List<Discount> discounts = new ArrayList<>();
                for (String regdNo : regdNos) {
                    Discount discountTemp = new Discount();
                    discountTemp.setCurrentYear(discount.getCurrentYear());
                    discountTemp.setStaffId(discount.getStaffId());
                    discountTemp.setDiscount(discount.getDiscount());
                    discountTemp.setParticulars(discount.getParticulars());
                    discountTemp.setRegdNo(regdNo);
                    discounts.add(discountTemp);
                }
                discountRepository.saveAllAndFlush(discounts);
                return true;
            }
        }
        else {
            discountRepository.save(discount);
            return true;
        }
    }
}
