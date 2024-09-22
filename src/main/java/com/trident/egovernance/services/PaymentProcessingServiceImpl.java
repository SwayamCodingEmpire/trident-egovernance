package com.trident.egovernance.services;

import com.trident.egovernance.entities.permanentDB.DuesDetails;
import com.trident.egovernance.entities.permanentDB.FeeCollection;
import com.trident.egovernance.entities.permanentDB.MrDetails;
import com.trident.egovernance.entities.permanentDB.Student;
import com.trident.egovernance.exceptions.InvalidStudentException;
import com.trident.egovernance.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.repositories.permanentDB.FeeCollectionRepository;
import com.trident.egovernance.repositories.permanentDB.MrDetailsRepository;
import com.trident.egovernance.repositories.permanentDB.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentProcessingServiceImpl {
    private final StudentRepository studentRepository;
    private final MasterTableServices masterTableServices;
    private final MrDetailsRepository mrDetailsRepository;
    private final DuesDetailsRepository duesDetailsRepository;
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(PaymentProcessingServiceImpl.class);
    private final FeeCollectionRepository feeCollectionRepository;

    public PaymentProcessingServiceImpl(StudentRepository studentRepository, MasterTableServices masterTableServices, MrDetailsRepository mrDetailsRepository, DuesDetailsRepository duesDetailsRepository, EntityManager entityManager,
                                        FeeCollectionRepository feeCollectionRepository) {
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
    public void processPayment(FeeCollection feeCollection,String regdNo){
        if(!studentRepository.existsById(regdNo)){
            throw new InvalidStudentException("Invalid Registration Number");
        }
        BigDecimal collectedFees = feeCollection.getCollectedFee();
        Student student = entityManager.getReference(Student.class,regdNo);
        feeCollection.setPaymentDate(String.valueOf(Year.now().getValue()));
        feeCollection.setStudent(student);
        long mrNo = feeCollectionRepository.getMaxMrNo()+1;
        List<MrDetails> mrDetailsList = new ArrayList<>();
        List<DuesDetails> duesDetails = duesDetailsRepository.findAllByRegdNoOrderByDeductionOrder(regdNo);
        long id = mrDetailsRepository.getMaxId();
        long slNo = 1;
        for (DuesDetails duesDetail : duesDetails) {
            feeCollection.setDueYear(duesDetail.getDueYear());
            MrDetails mrDetails1 = new MrDetails();
            if(collectedFees.compareTo(BigDecimal.ZERO)>0) {
                mrDetails1.setParticulars(duesDetail.getDescription());
                mrDetails1.setFeeCollection(feeCollection);
                mrDetails1.setSlNo(slNo);
                mrDetails1.setId(id + 1);
                MrDetails mrDetails = createMrDetails(duesDetail,collectedFees,id,slNo);
                collectedFees = updateDuesAndCollectedFees(duesDetail,collectedFees);
                mrDetailsList.add(mrDetails);
            }
            else if(collectedFees.compareTo(BigDecimal.ZERO)==0){
                logger.info("Collected fees is 0");
                mrDetailsRepository.saveAllAndFlush(mrDetailsList);
                break;
            }
            id++;
            slNo++;
        }
        if(collectedFees.compareTo(BigDecimal.ZERO)>0){
            logger.info(collectedFees.toString());
            throw new InvalidStudentException("Amount paid is greater than dues");
        }
        duesDetailsRepository.saveAllAndFlush(duesDetails);
        String session = fetchCurrentSessionForStudent(regdNo);
        feeCollection.setMrNo(mrNo);
        feeCollection.setSessionId(session);
        feeCollection.setMrDetails(mrDetailsList);
        feeCollectionRepository.save(feeCollection);
        logger.info(feeCollectionRepository.getMaxMrNo().toString());
    }

    private MrDetails createMrDetails(DuesDetails duesDetails, BigDecimal collectedFees,Long id, long slNo){
        MrDetails mrDetails = new MrDetails();
        mrDetails.setSlNo(slNo);
        mrDetails.setId(id);
        if(collectedFees.compareTo(duesDetails.getAmountDue())<0){
            mrDetails.setAmount(collectedFees);
        }
        else {
            mrDetails.setAmount(duesDetails.getAmountDue());
        }
        return mrDetails;
    }

    private BigDecimal updateDuesAndCollectedFees(DuesDetails duesDetail, BigDecimal collectedFees){
        if(collectedFees.compareTo(duesDetail.getAmountDue())<0){
            duesDetail.setAmountDue(duesDetail.getAmountDue().subtract(collectedFees));
            duesDetail.setAmountPaid(collectedFees);
            return BigDecimal.ZERO;
        }
        else {
            BigDecimal remaining = collectedFees.subtract(duesDetail.getAmountDue());
            duesDetail.setAmountDue(BigDecimal.ZERO);
            duesDetail.setAmountPaid(duesDetail.getAmountDue());
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

}
