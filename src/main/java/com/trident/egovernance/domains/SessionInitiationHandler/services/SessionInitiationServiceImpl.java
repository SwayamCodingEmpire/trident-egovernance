package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.SessionInitiationHandler.SessionInitiationService;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.dto.SessionInitiationData;
import com.trident.egovernance.dto.StudentOnlyDTO;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

    public SessionInitiationServiceImpl(PlatformTransactionManager platformTransactionManager, DuesDetailBackupServiceImpl duesDetailBackupService, AdjustmentBackupServiceImpl adjustmentBackupService, DiscountBackUpServiceImpl discountBackUpService, HostelBackupServiceImpl hostelBackupService, StudentRepository studentRepository, TransportBackupServiceimpl transportBackupServiceimpl) {
        this.platformTransactionManager = platformTransactionManager;
        this.duesDetailBackupService = duesDetailBackupService;
        this.adjustmentBackupService = adjustmentBackupService;
        this.discountBackUpService = discountBackUpService;
        this.hostelBackupService = hostelBackupService;
        this.studentRepository = studentRepository;
        this.transportBackupServiceimpl = transportBackupServiceimpl;
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
            List<String> regdNos = sessionInitiationData.regdNos();
            CompletableFuture<Boolean> transportbackUp = transportBackupServiceimpl.transferToOldTransport(regdNos,status);
            CompletableFuture<Boolean> hostelBackUp = hostelBackupService.transferToOldHostel(regdNos,status);
            CompletableFuture<Boolean> adjustmentbackUp = adjustmentBackupService.transferToOldAdjustment(regdNos,status);
            CompletableFuture<Boolean> discountbackUp = discountBackUpService.transferToOldDiscount(regdNos,status);
            CompletableFuture<Boolean> duesDetailsbackUp = duesDetailBackupService.transferToOldDuesDetails(regdNos,status);
//            duesDetailBackupService.saveToDuesDetails(previousYearDues);
            CompletableFuture<Void> allOf = CompletableFuture.allOf(transportbackUp, hostelBackUp, adjustmentbackUp, discountbackUp, duesDetailsbackUp);
            allOf.join();
            platformTransactionManager.commit(status);
            logger.info("Transaction Commited Successfully");
            return true;
        }catch (Exception e){
            status.setRollbackOnly();
            platformTransactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }

    private void promoteStudent(List<String> regdNos){

    }

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
