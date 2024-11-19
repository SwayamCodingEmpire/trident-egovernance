package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.SessionInitiationHandler.SessionInitiationService;
import com.trident.egovernance.domains.nsrHandler.services.DuesInitiationServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;
import com.trident.egovernance.global.repositories.permanentDB.*;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class SessionInitiationServiceImpl implements SessionInitiationService {
    private final PlatformTransactionManager platformTransactionManager;
    private final DuesDetailBackupServiceImpl duesDetailBackupService;
    private final AdjustmentBackupServiceImpl adjustmentBackupService;
    private final DiscountBackUpServiceImpl discountBackUpService;
    private final HostelBackupServiceImpl hostelBackupService;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(SessionInitiationServiceImpl.class);
    private final TransportBackupServiceimpl transportBackupServiceimpl;
    private final NotpromotedRepository notpromotedRepository;
    private final HostelRepository hostelRepository;
    private final FeeCollectionRepository feeCollectionRepository;
    private final MrDetailsRepository mrDetailsRepository;
    private final MasterTableServicesImpl masterTableServicesImpl;
    private final EntityManager entityManager;
    private final TransportRepository transportRepository;
    private final SessionsRepository sessionsRepository;
    private final DuesInitiationServiceImpl duesInitiationServiceImpl;

    public SessionInitiationServiceImpl(PlatformTransactionManager platformTransactionManager, DuesDetailBackupServiceImpl duesDetailBackupService, AdjustmentBackupServiceImpl adjustmentBackupService, DiscountBackUpServiceImpl discountBackUpService, HostelBackupServiceImpl hostelBackupService, StudentRepository studentRepository, TransportBackupServiceimpl transportBackupServiceimpl, NotpromotedRepository notpromotedRepository, HostelRepository hostelRepository, FeeCollectionRepository feeCollectionRepository, MrDetailsRepository mrDetailsRepository, MasterTableServicesImpl masterTableServicesImpl, EntityManager entityManager, TransportRepository transportRepository, SessionsRepository sessionsRepository, DuesInitiationServiceImpl duesInitiationServiceImpl) {
        this.platformTransactionManager = platformTransactionManager;
        this.duesDetailBackupService = duesDetailBackupService;
        this.adjustmentBackupService = adjustmentBackupService;
        this.discountBackUpService = discountBackUpService;
        this.hostelBackupService = hostelBackupService;
        this.studentRepository = studentRepository;
        this.transportBackupServiceimpl = transportBackupServiceimpl;
        this.notpromotedRepository = notpromotedRepository;
        this.hostelRepository = hostelRepository;
        this.feeCollectionRepository = feeCollectionRepository;
        this.mrDetailsRepository = mrDetailsRepository;
        this.masterTableServicesImpl = masterTableServicesImpl;
        this.entityManager = entityManager;
        this.transportRepository = transportRepository;
        this.sessionsRepository = sessionsRepository;
        this.duesInitiationServiceImpl = duesInitiationServiceImpl;
    }

    public List<StudentOnlyDTO> getStudentsForPromotion(SessionInitiationDTO sessionInitiationDTO) {
        return studentRepository.findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(sessionInitiationDTO.admYear(), sessionInitiationDTO.course(), sessionInitiationDTO.regdYear(), sessionInitiationDTO.studentType());

    }

    public Boolean initiateNewSession(SessionInitiationData sessionInitiationData){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = platformTransactionManager.getTransaction(def);
        try{
            logger.info(sessionInitiationData.toString());
            Set<String> regdNos = sessionInitiationData.regdNos();
            CompletableFuture<Boolean> transportbackUp = transportBackupServiceimpl.transferToOldTransport(regdNos,status);
            CompletableFuture<Boolean> hostelBackUp = hostelBackupService.transferToOldHostel(regdNos,status);
            CompletableFuture<Boolean> adjustmentbackUp = adjustmentBackupService.transferToOldAdjustment(regdNos,status);
            CompletableFuture<Boolean> discountbackUp = discountBackUpService.transferToOldDiscount(regdNos,status);
            CompletableFuture<Boolean> duesDetailsbackUp = duesDetailBackupService.transferToOldDuesDetails(regdNos,status,sessionInitiationData.sessionId());
//            duesDetailBackupService.saveToDuesDetails(previousYearDues);
            CompletableFuture<Void> allOf = CompletableFuture.allOf(transportbackUp, hostelBackUp, adjustmentbackUp, discountbackUp, duesDetailsbackUp);
            allOf.join();
            promoteStudent(sessionInitiationData,status);
            platformTransactionManager.commit(status);
            logger.info("Transaction Commited Successfully");
            return true;
        }catch (Exception e){
            status.setRollbackOnly();
            platformTransactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }

    public boolean initiateDueDetails(SessionInitiationData sessionInitiationData){return true;}

    private int promoteStudent(SessionInitiationData sessionInitiationData,TransactionStatus transactionStatus){
//        Set<FeeCollectionDTOWithRegdNo> feeCollectionDTOWithRegdNos = feeCollectionRepository.findAllByMrDetails_ParticularsAndSessionIdNew("HOSTEL ADVANCE", sessionInitiationData.prevSessionId(), sessionInitiationData.regdNos());
//        Set<String> hostelNotOpted = new HashSet<>(sessionInitiationData.regdNos());
//        Set<Long> hostelOptedMrNos = new HashSet<>();
//        Set<String> hostelOpted = new HashSet<>();
////                .map(FeeCollectionDTO::regdNo)
////                .collect(Collectors.toSet());
//        for(FeeCollectionDTOWithRegdNo feeCollectionDTOWithRegdNo : feeCollectionDTOWithRegdNos){
//            hostelOptedMrNos.add(feeCollectionDTOWithRegdNo.feeCollection().getMrNo());
//            hostelOpted.add(feeCollectionDTOWithRegdNo.regdNo());
//        }
//        hostelNotOpted.removeAll(hostelOpted);
//        hostelRepository.updateHostelByRegdNoInNotOpted(BooleanString.NO,hostelNotOpted, HostelChoice.NONE);
//        hostelRepository.updateHostelByRegdNoIn(BooleanString.YES,hostelOpted);
//        FeeTypes hostelFees = masterTableServicesImpl.getFeeTypesByFeeGroupAndSemester("HOSTELFEES",sessionInitiationData.currentYear()*2+1);
////        mrDetailsRepository.updateMrDetailsByIdAndParticular(hostelOptedMrNos,"HOSTEL ADVANCE");
//        StringBuilder queryBuilder = new StringBuilder("UPDATE FeeCollection fc SET ");
//
//// Update the 3 fields in FeeCollection with conditional CASE statements
//        queryBuilder.append("fc.dueYear = CASE ");
//        for (int i = 0; i < hostelOptedMrNos.size(); i++) {
//            queryBuilder.append("WHEN fc.mrNo = :mrNo").append(i).append(" THEN :dueYear").append(i).append(" ");
//        }
//        queryBuilder.append("END, ");
//
//        queryBuilder.append("fc.sessionId = CASE ");
//        for (int i = 0; i < hostelOptedMrNos.size(); i++) {
//            queryBuilder.append("WHEN fc.mrNo = :mrNo").append(i).append(" THEN :sessionId").append(i).append(" ");
//        }
//        queryBuilder.append("END, ");
//
////        queryBuilder.append("fc.ddNo = CASE ");
////        for (int i = 0; i < updates.size(); i++) {
////            queryBuilder.append("WHEN fc.mrNo = :mrNo").append(i).append(" THEN :ddNo").append(i).append(" ");
////        }
////        queryBuilder.append("END ");
//
//// Update the 2 fields in MrDetails with conditional CASE statements
//        queryBuilder.append("UPDATE MrDetails md SET ");
//        queryBuilder.append("md.particulars = CASE ");
//        for (int i = 0; i < hostelOptedMrNos.size(); i++) {
//            queryBuilder.append("WHEN md.feeCollection.mrNo = :mrNo").append(i).append(" THEN :particulars").append(i).append(" ");
//        }
//        queryBuilder.append("END, ");
//
////        queryBuilder.append("md.amount = CASE ");
////        for (int i = 0; i < updates.size(); i++) {
////            queryBuilder.append("WHEN md.feeCollection.mrNo = :mrNo").append(i).append(" THEN :amount").append(i).append(" ");
////        }
////        queryBuilder.append("END ");
//
//// Add the WHERE clause
//        queryBuilder.append("WHERE fc.mrNo IN (:mrNos) AND md.feeCollection.mrNo IN (:mrNos)");
//
//// Create the native query
//        var query = entityManager.createNativeQuery(queryBuilder.toString());
//
//// Set parameters for FeeCollection updates (collectedFee, paymentMode, ddNo)
//        for (Long hostelOptedMrNo : hostelOptedMrNos) {
//            query.setParameter("mrNos" + hostelOptedMrNo, hostelOptedMrNo);
//            query.setParameter("dueYear" + hostelOptedMrNo, sessionInitiationData.currentYear()*2+1);
//            query.setParameter("sessionId" + hostelOptedMrNo, sessionInitiationData.sessionId());
//        }
//
//// Set parameters for MrDetails updates (particulars, amount)
//        for (Long hostelOptedMrNo : hostelOptedMrNos) {
//            query.setParameter("mrNo" + hostelOptedMrNo, hostelOptedMrNo);
//            query.setParameter("particulars" + hostelOptedMrNo, hostelFees.getDescription());
//        }
//
//// Set the mrNos parameter for the WHERE clause
//        query.setParameter("mrNos", hostelOptedMrNos);
//
//// Execute the update
//        int rowsUpdated = query.executeUpdate();
//        return rowsUpdated;
        try {
            Set<FeeCollectionDTOWithRegdNo> feeCollectionDTOWithRegdNos = feeCollectionRepository.findAllByMrDetails_ParticularsAndSessionIdNew("HOSTEL ADVANCE", sessionInitiationData.prevSessionId(), sessionInitiationData.regdNos());

            Set<String> hostelNotOpted = new HashSet<>(sessionInitiationData.regdNos());
            Set<Long> hostelOptedMrNos = new HashSet<>();
            Set<String> hostelOpted = new HashSet<>();

            // Collecting MrNos and RegdNos
            for (FeeCollectionDTOWithRegdNo feeCollectionDTOWithRegdNo : feeCollectionDTOWithRegdNos) {
                hostelOptedMrNos.add(feeCollectionDTOWithRegdNo.feeCollection().getMrNo());
                hostelOpted.add(feeCollectionDTOWithRegdNo.regdNo());
            }

            hostelNotOpted.removeAll(hostelOpted);

            // Update hostel choice statuses
            hostelRepository.updateHostelByRegdNoInNotOpted(BooleanString.NO, hostelNotOpted, HostelChoice.NONE);
            hostelRepository.updateHostelByRegdNoIn(BooleanString.YES, hostelOpted);

            // Fetching the FeeTypes for Hostel fees
            FeeTypes hostelFees = masterTableServicesImpl.getFeeTypesByFeeGroupAndSemester("HOSTELFEES", sessionInitiationData.currentYear() * 2 + 1);

            // 1. First update batch: Update FeeCollection
//        StringBuilder feeCollectionQuery = new StringBuilder("UPDATE FeeCollection fc SET ");
//
//        // FeeCollection update (dueYear and sessionId)
//        feeCollectionQuery.append("fc.dueYear = CASE ");
//        for (Long mrNo : hostelOptedMrNos) {
//            feeCollectionQuery.append("WHEN fc.mrNo = :mrNo").append(mrNo).append(" THEN :dueYear").append(mrNo).append(" ");
//        }
//        feeCollectionQuery.append("END, ");
//
//        feeCollectionQuery.append("fc.sessionId = CASE ");
//        for (Long mrNo : hostelOptedMrNos) {
//            feeCollectionQuery.append("WHEN fc.mrNo = :mrNo").append(mrNo).append(" THEN :sessionId").append(mrNo).append(" ");
//        }
//        feeCollectionQuery.append("END ");
//
//        // WHERE clause for FeeCollection
//        feeCollectionQuery.append("WHERE fc.mrNo IN (:mrNos)");
//
//        // Create and execute the FeeCollection update query
//        var feeCollectionQueryObj = entityManager.createNativeQuery(feeCollectionQuery.toString());
//
//        // Set parameters for FeeCollection update
//        for (Long mrNo : hostelOptedMrNos) {
//            feeCollectionQueryObj.setParameter("mrNo" + mrNo, mrNo);
//            feeCollectionQueryObj.setParameter("dueYear" + mrNo, sessionInitiationData.currentYear() * 2 + 1);
//            feeCollectionQueryObj.setParameter("sessionId" + mrNo, sessionInitiationData.sessionId());
//        }
//
//        // Set the mrNos parameter for the WHERE clause
//        feeCollectionQueryObj.setParameter("mrNos", hostelOptedMrNos);
//
//        // Execute the update for FeeCollection
//        int rowsUpdatedFeeCollection = feeCollectionQueryObj.executeUpdate();
//
//        // 2. Second update batch: Update MrDetails
//        StringBuilder mrDetailsQuery = new StringBuilder("UPDATE MrDetails md SET ");
//
//        // MrDetails update (particulars)
//        mrDetailsQuery.append("md.particulars = CASE ");
//        for (Long mrNo : hostelOptedMrNos) {
//            mrDetailsQuery.append("WHEN md.mrNo = :mrNo").append(mrNo).append(" THEN :particulars").append(mrNo).append(" ");
//        }
//        mrDetailsQuery.append("END ");
//
//        // WHERE clause for MrDetails
//        mrDetailsQuery.append("WHERE md.mrNo IN (:mrNos)");
//
//        // Create and execute the MrDetails update query
//        var mrDetailsQueryObj = entityManager.createNativeQuery(mrDetailsQuery.toString());
//
//        // Set parameters for MrDetails update
//        for (Long mrNo : hostelOptedMrNos) {
//            mrDetailsQueryObj.setParameter("mrNo" + mrNo, mrNo);
//            mrDetailsQueryObj.setParameter("particulars" + mrNo, hostelFees.getDescription());
//        }
//
//        // Set the mrNos parameter for the WHERE clause
//        mrDetailsQueryObj.setParameter("mrNos", hostelOptedMrNos);
//
//        // Execute the update for MrDetails
//        int rowsUpdatedMrDetails = mrDetailsQueryObj.executeUpdate();
            int admissionYear = masterTableServicesImpl.getAdmissionYearFromSession(sessionInitiationData.sessionId(), sessionInitiationData.courses(), sessionInitiationData.currentYear(), sessionInitiationData.studentType());
            sessionsRepository.save(new Sessions(
                    sessionInitiationData.sessionId(),
                    sessionInitiationData.startDate(),
                    null,
                    sessionInitiationData.courses().getDisplayName(),
                    sessionInitiationData.currentYear() + 1,
                    sessionInitiationData.prevSessionId(),
                    admissionYear,
                    sessionInitiationData.studentType().name()
            ));
            if (masterTableServicesImpl.endSession(Date.valueOf(LocalDate.now()), sessionInitiationData.prevSessionId(), sessionInitiationData.courses(), sessionInitiationData.currentYear(), sessionInitiationData.studentType())) {
                return feeCollectionRepository.updateFeeCollectionByMrForHostelRegistered(sessionInitiationData.currentYear() + 1, sessionInitiationData.sessionId(), hostelOptedMrNos) + mrDetailsRepository.updateMrDetailsByMrNoForHostelRegistered(hostelFees.getDescription(), hostelOptedMrNos) + studentRepository.updateStudentCurrentYearByRegdNo(sessionInitiationData.regdNos());
            }
            throw new RuntimeException("Unable to start session");
        }catch (Exception e){
            logger.error(e.toString());
            transactionStatus.setRollbackOnly();
            return 0;
        }
    }

//    public boolean setDuesDetails(SessionInitiationData sessionInitiationData, TransactionStatus status){
//        duesInitiationServiceImpl.initiateDuesDetails(studentRepository.findByRegdNoIn(sessionInitiationData.regdNos()))
//        return true;}



//    @Transactional
//    public Boolean initiateNewSessionTemp(SessionInitiationDTO sessionInitiationDTO){
//        List<Student> studentList = studentRepository.findAllByCourseAndCurrentYear(sessionInitiationDTO.course(), sessionInitiationDTO.regdYear());
//        List<String> regdNos = studentList.stream()
//                .map(Student::getRegdNo)
//                .collect(Collectors.toList());
//        logger.info(regdNos.toString());
//        logger.info(studentList.toString());
//        transportBackupServiceimpl.transferToOldTransport(regdNos);
//        logger.info(studentList.toString());
//        logger.info(sessionInitiationDTO.toString());
//        hostelBackupService.transferToOldHostel(regdNos);
//        adjustmentBackupService.transferToOldAdjustment(regdNos);
//        discountBackUpService.transferToOldDiscount(regdNos);
//        List<DuesDetails> previousYearDues = duesDetailBackupService.transferToOldDuesDetails(regdNos);
//        logger.info(previousYearDues.toString());
//        return true;
//    }
}
