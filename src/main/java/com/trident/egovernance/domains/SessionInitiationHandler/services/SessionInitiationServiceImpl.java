package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.domains.SessionInitiationHandler.SessionInitiationService;
import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessionInitiationServiceImpl implements SessionInitiationService {
    private final DuesDetailBackupServiceImpl duesDetailBackupService;
    private final AdjustmentBackupServiceImpl adjustmentBackupService;
    private final DiscountBackUpServiceImpl discountBackUpService;
    private final HostelBackupServiceImpl hostelBackupService;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(SessionInitiationServiceImpl.class);
    private final TransportBackupServiceimpl transportBackupServiceimpl;

    public SessionInitiationServiceImpl(DuesDetailBackupServiceImpl duesDetailBackupService, AdjustmentBackupServiceImpl adjustmentBackupService, DiscountBackUpServiceImpl discountBackUpService, HostelBackupServiceImpl hostelBackupService, StudentRepository studentRepository, TransportBackupServiceimpl transportBackupServiceimpl) {
        this.duesDetailBackupService = duesDetailBackupService;
        this.adjustmentBackupService = adjustmentBackupService;
        this.discountBackUpService = discountBackUpService;
        this.hostelBackupService = hostelBackupService;
        this.studentRepository = studentRepository;
        this.transportBackupServiceimpl = transportBackupServiceimpl;
    }


    @Transactional
    public Boolean initiateNewSession(SessionInitiationDTO sessionInitiationDTO){
        List<Student> studentList = studentRepository.findAllByAdmissionYearAndCourseAndCurrentYearAndStudentType(sessionInitiationDTO.admYear(), sessionInitiationDTO.course(), sessionInitiationDTO.regdYear(), sessionInitiationDTO.studentType());
        List<String> regdNos = studentList.stream()
                        .map(Student::getRegdNo)
                                .collect(Collectors.toList());
        logger.info(regdNos.toString());
        logger.info(studentList.toString());
        transportBackupServiceimpl.saveToOldTransport(regdNos);
        hostelBackupService.saveToOldHostel(regdNos);
        adjustmentBackupService.saveToOldAdjustment(regdNos);
        discountBackUpService.saveToOldDiscount(regdNos);
        List<DuesDetails> previousYearDues = duesDetailBackupService.saveToOldDuesDetails(regdNos);
        transportBackupServiceimpl.deleteFromTransport(regdNos);
        hostelBackupService.deleteFromHostel(regdNos);
        adjustmentBackupService.deleteFromAdjustments(regdNos);
        discountBackUpService.deleteFromDiscounts(regdNos);
        duesDetailBackupService.deleteFromDuesDetails(regdNos);
        duesDetailBackupService.saveToDuesDetails(previousYearDues);
        return true;
    }

    private void promoteStudent(List<String> regdNos){

    }

    @Transactional
    public Boolean initiateNewSessionTemp(SessionInitiationDTO sessionInitiationDTO){
        List<Student> studentList = studentRepository.findAllByCourseAndCurrentYear(sessionInitiationDTO.course(), sessionInitiationDTO.regdYear());
        List<String> regdNos = studentList.stream()
                .map(Student::getRegdNo)
                .collect(Collectors.toList());
        logger.info(regdNos.toString());
        logger.info(studentList.toString());
        transportBackupServiceimpl.saveToOldTransport(regdNos);
        logger.info(studentList.toString());
        logger.info(sessionInitiationDTO.toString());
        hostelBackupService.saveToOldHostel(regdNos);
        adjustmentBackupService.saveToOldAdjustment(regdNos);
        discountBackUpService.saveToOldDiscount(regdNos);
        List<DuesDetails> previousYearDues = duesDetailBackupService.saveToOldDuesDetails(regdNos);
        logger.info(previousYearDues.toString());

//        transportBackupServiceimpl.deleteFromTransport(regdNos);
//        hostelBackupService.deleteFromHostel(regdNos);
//        adjustmentBackupService.deleteFromAdjustments(regdNos);
//        discountBackUpService.deleteFromDiscounts(regdNos);
//        duesDetailBackupService.deleteFromDuesDetails(regdNos);
//        duesDetailBackupService.saveToDuesDetails(previousYearDues);
        return true;
    }
}
