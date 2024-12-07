package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.SessionInitiationHandler.SessionInitiationService;
import com.trident.egovernance.domains.nsrHandler.services.DuesInitiationServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.DatabaseException;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final DuesDetailsReInitiationServiceImpl duesDetailsReInitiationServiceImpl;

    public SessionInitiationServiceImpl(PlatformTransactionManager platformTransactionManager, DuesDetailBackupServiceImpl duesDetailBackupService, AdjustmentBackupServiceImpl adjustmentBackupService, DiscountBackUpServiceImpl discountBackUpService, HostelBackupServiceImpl hostelBackupService, StudentRepository studentRepository, TransportBackupServiceimpl transportBackupServiceimpl, NotpromotedRepository notpromotedRepository, HostelRepository hostelRepository, FeeCollectionRepository feeCollectionRepository, MrDetailsRepository mrDetailsRepository, MasterTableServicesImpl masterTableServicesImpl, EntityManager entityManager, TransportRepository transportRepository, SessionsRepository sessionsRepository, DuesInitiationServiceImpl duesInitiationServiceImpl, DuesDetailsReInitiationServiceImpl duesDetailsReInitiationServiceImpl) {
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
        this.duesDetailsReInitiationServiceImpl = duesDetailsReInitiationServiceImpl;
    }

    public List<StudentOnlyDTO> getStudentsForPromotion(SessionInitiationDTO sessionInitiationDTO) {
        return studentRepository.findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(sessionInitiationDTO.admYear(), sessionInitiationDTO.course(), sessionInitiationDTO.regdYear(), sessionInitiationDTO.studentType());

    }

    @Transactional
    public Boolean initiateNewSession(SessionInitiationData sessionInitiationData){
        try{
            logger.info(sessionInitiationData.toString());
            Set<String> regdNos = sessionInitiationData.regdNos();
            Boolean transportbackUp = transportBackupServiceimpl.transferToOldTransport(regdNos);
            Boolean hostelBackUp = hostelBackupService.transferToOldHostel(regdNos);
            Boolean adjustmentbackUp = adjustmentBackupService.transferToOldAdjustment(regdNos);
            Boolean discountbackUp = discountBackUpService.transferToOldDiscount(regdNos);
            Boolean duesDetailsbackUp = duesDetailBackupService.transferToOldDuesDetails(regdNos,sessionInitiationData.sessionId());
//            duesDetailBackupService.saveToDuesDetails(previousYearDues);
            logger.info("Transaction Commited Successfully");
            return true;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean promoteStudent(SessionInitiationData sessionInitiationData,TransactionStatus transactionStatus){
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
            feeCollectionRepository.updateFeeCollectionByMrForHostelRegistered(sessionInitiationData.currentYear() + 1, sessionInitiationData.sessionId(), hostelOptedMrNos);
            mrDetailsRepository.updateMrDetailsByMrNoForHostelRegistered(hostelFees.getDescription(), hostelOptedMrNos);
            studentRepository.updateStudentCurrentYearByRegdNo(sessionInitiationData.regdNos());
            List<DuesDetailsInitiationDTO> duesDetailsInitiationDTOS = studentRepository.findStudentByRegdNo(sessionInitiationData.regdNos());
            for (FeeCollectionDTOWithRegdNo f : feeCollectionDTOWithRegdNos) {
                f.feeCollection().getMrDetails().forEach(
                        mrDetails -> mrDetails.setParticulars
                                (
                                        hostelFees.getDescription()
                                )
                );
            }
            return duesDetailsReInitiationServiceImpl.reInitiateDuesDetails(duesDetailsInitiationDTOS,feeCollectionDTOWithRegdNos);
        }catch (Exception e){
            logger.error(e.toString());
            return false;
        }
    }

    @Override
    public Sessions createNewSession(SessionInitiationData sessionInitiationData) {
        try{
            int admissionYear = masterTableServicesImpl.getAdmissionYearFromSession(sessionInitiationData.sessionId(), sessionInitiationData.course(), sessionInitiationData.currentYear(), sessionInitiationData.studentType());
            return sessionsRepository.save(new Sessions(
                    sessionInitiationData.sessionId(),
                    sessionInitiationData.startDate(),
                    null,
                    sessionInitiationData.course().getDisplayName(),
                    sessionInitiationData.currentYear() + 1,
                    sessionInitiationData.prevSessionId(),
                    sessionInitiationData.admYear(),
                    sessionInitiationData.studentType().name()
            ));
        }catch (Exception e){
            logger.error(e.toString());
            throw new DatabaseException("Could not create new session");
        }
    }
}
