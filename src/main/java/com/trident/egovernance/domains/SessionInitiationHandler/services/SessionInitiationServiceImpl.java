package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.SessionInitiationHandler.SessionInitiationService;
import com.trident.egovernance.domains.nsrHandler.services.DuesInitiationServiceImpl;
import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.DatabaseException;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.entities.permanentDB.Notpromoted;
import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;
import com.trident.egovernance.global.helpers.SessionIdId;
import com.trident.egovernance.global.repositories.permanentDB.*;
import com.trident.egovernance.global.services.MasterTableServicesImpl;
import com.trident.egovernance.global.services.MiscellaniousServices;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionInitiationServiceImpl implements SessionInitiationService {
    private final MiscellaniousServices miscellaniousServices;
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

    public SessionInitiationServiceImpl(MiscellaniousServices miscellaniousServices, PlatformTransactionManager platformTransactionManager, DuesDetailBackupServiceImpl duesDetailBackupService, AdjustmentBackupServiceImpl adjustmentBackupService, DiscountBackUpServiceImpl discountBackUpService, HostelBackupServiceImpl hostelBackupService, StudentRepository studentRepository, TransportBackupServiceimpl transportBackupServiceimpl, NotpromotedRepository notpromotedRepository, HostelRepository hostelRepository, FeeCollectionRepository feeCollectionRepository, MrDetailsRepository mrDetailsRepository, MasterTableServicesImpl masterTableServicesImpl, EntityManager entityManager, TransportRepository transportRepository, SessionsRepository sessionsRepository, DuesInitiationServiceImpl duesInitiationServiceImpl, DuesDetailsReInitiationServiceImpl duesDetailsReInitiationServiceImpl) {
        this.miscellaniousServices = miscellaniousServices;
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

    public List<StudentCourse> getStudentsForPromotion(SessionInitiationDTO sessionInitiationDTO) {
        return studentRepository.findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(sessionInitiationDTO.admYear(), sessionInitiationDTO.course(), sessionInitiationDTO.regdYear(), sessionInitiationDTO.studentType());

    }

    @Transactional
    public Boolean initiateNewSession(SessionInitiationData sessionInitiationData){
        try{
            String newSessionId = miscellaniousServices.incrementYearRange(sessionInitiationData.sessionId());
            Sessions sessions = sessionsRepository.findById(new SessionIdId(sessionInitiationData.course().getDisplayName(), sessionInitiationData.currentYear(), sessionInitiationData.admYear(), sessionInitiationData.studentType().getEnumName())).orElseThrow(()-> new RecordNotFoundException("Session Not Found"));
            Sessions savedSessions = sessionsRepository.saveAndFlush(new Sessions(newSessionId, Date.valueOf(LocalDate.now()), null, sessionInitiationData.course().getDisplayName(), sessionInitiationData.currentYear()+1, sessionInitiationData.sessionId(), sessionInitiationData.admYear(), sessionInitiationData.studentType().getEnumName()));
            if(savedSessions == null){
                throw new DatabaseException("Session Not Found");
            }
            logger.info(sessionInitiationData.toString());
            return backUpAndPromoteStudent(sessionInitiationData);
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean backUpAndPromoteStudent(SessionInitiationData sessionInitiationData){
        try
        {
            logger.info(sessionInitiationData.toString());
            Set<String> regdNos = sessionInitiationData.regdNos();
            Boolean transportbackUp = transportBackupServiceimpl.transferToOldTransport(regdNos);
            Boolean hostelBackUp = hostelBackupService.transferToOldHostel(regdNos);
            Boolean adjustmentbackUp = adjustmentBackupService.transferToOldAdjustment(regdNos);
            logger.info("Old Adjustment backed up");
            Boolean discountbackUp = discountBackUpService.transferToOldDiscount(regdNos);
            logger.info("Disocunt completed");
            Boolean duesDetailsbackUp = duesDetailBackupService.transferToOldDuesDetails(regdNos,sessionInitiationData.sessionId());
            logger.info("Dues Details completed");
//            duesDetailBackupService.saveToDuesDetails(previousYearDues);
            logger.info("Transaction Commited Successfully");
            if(!sessionInitiationData.promotionType()){
                promoteNotPromotedStudent(sessionInitiationData);
            }
            else if(promoteStudent(sessionInitiationData)){
                return true;
            }
            throw new RuntimeException("Backup And Promotion failed");
        }catch (Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean promoteNotPromotedStudent(SessionInitiationData sessionInitiationData){
        try {
            return studentPromotion(sessionInitiationData);
        }catch (Exception e){
            logger.error(e.toString());
            throw new RuntimeException("Session promotion failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean studentPromotion(SessionInitiationData sessionInitiationData){
        try{
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
            if(!hostelOptedMrNos.isEmpty()){
                feeCollectionRepository.updateFeeCollectionByMrForHostelRegistered(sessionInitiationData.currentYear() + 1, sessionInitiationData.sessionId(), hostelOptedMrNos);
                mrDetailsRepository.updateMrDetailsByMrNoForHostelRegistered(hostelFees.getDescription(), hostelOptedMrNos);
            }
            logger.info(sessionInitiationData.regdNos().toString());
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
            if(!sessionInitiationData.notPromoted().isEmpty()){
                addToNotPromotedList(sessionInitiationData);
            }
            return duesDetailsReInitiationServiceImpl.reInitiateDuesDetails(duesDetailsInitiationDTOS,feeCollectionDTOWithRegdNos);
        }catch (Exception e){
            logger.error(e.toString());
            throw new RuntimeException("Session promotion failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean promoteStudent(SessionInitiationData sessionInitiationData){
        try {
            if(!masterTableServicesImpl.endSession(new Date(System.currentTimeMillis()), sessionInitiationData.sessionId(), sessionInitiationData.course(), sessionInitiationData.currentYear(), sessionInitiationData.studentType(), sessionInitiationData.admYear())){
                throw new RuntimeException("Session Ending Failed");
            }
//            Sessions sessions = createNewSession(sessionInitiationData);
//            if(sessions == null){
//                throw new RuntimeException("Session could not be created");
//            }
            return studentPromotion(sessionInitiationData);
        }catch (Exception e){
            logger.error(e.toString());
            throw new RuntimeException("Session promotion failed");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addToNotPromotedList(SessionInitiationData sessionInitiationData){
        List<Notpromoted> notPromotedSet = sessionInitiationData.notPromoted().stream()
                .map(regdNo -> new Notpromoted(regdNo, sessionInitiationData.currentYear(), sessionInitiationData.sessionId()))
                .toList();
        notpromotedRepository.saveAllAndFlush(notPromotedSet);
    }

    public List<Notpromoted> getNotPromoted(Optional<String> regdNo,
                                            Optional<Integer> currentYear,
                                            Optional<String> sessionId) {
        return notpromotedRepository.findNotPromoted(
                regdNo.orElse(null),
                currentYear.orElse(null),
                sessionId.orElse(null)
        );
    }
}
