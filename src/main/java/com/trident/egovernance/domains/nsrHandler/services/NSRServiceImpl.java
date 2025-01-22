package com.trident.egovernance.domains.nsrHandler.services;

import com.trident.egovernance.dto.DuesDetailsInitiationDTO;
import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.exceptions.RecordAlreadyExistsException;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.helpers.*;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.redisRepositories.NSRRepository;
import com.trident.egovernance.global.services.CourseFetchingServiceImpl;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MapperServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
class NSRServiceImpl implements NSRService {
    private final UserCreationService userCreationService;
    private final DuesInitiationService duesInitiationServiceImpl;
    private final PlatformTransactionManager platformTransactionManager;
    private final MapperService mapperService;
    private final CourseFetchingServiceImpl courseFetchingService;
    private final Logger logger = LoggerFactory.getLogger(NSRServiceImpl.class);
    private final NSRRepository nsrRepository;
    private final StudentRepository studentRepository;

    private final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final int PASSWORD_LENGTH = 12;  // Adjust the length as needed
    private final SecureRandom random = new SecureRandom();
    private final EmailSenderServiceImpl emailSenderServiceImpl;


    public NSRServiceImpl(UserCreationService userCreationService, DuesInitiationService duesInitiationServiceImpl, PlatformTransactionManager platformTransactionManager, MapperServiceImpl mapperService, CourseFetchingServiceImpl courseFetchingService, NSRRepository nsrRepository, StudentRepository studentRepository, EmailSenderServiceImpl emailSenderServiceImpl) {
        this.userCreationService = userCreationService;
        this.duesInitiationServiceImpl = duesInitiationServiceImpl;
        this.platformTransactionManager = platformTransactionManager;
        this.mapperService = mapperService;
        this.courseFetchingService = courseFetchingService;
        this.nsrRepository = nsrRepository;
        this.studentRepository = studentRepository;
        this.emailSenderServiceImpl = emailSenderServiceImpl;
    }

    @Override
    public NSRDto postNSRData(NSR nsr){
        if(!nsrRepository.existsById(nsr.getJeeApplicationNo())){
        if(nsr.getRankType().equals(RankType.JEE)){
            nsr.setAieeeRank(nsr.getRank().toString());
        }
        else {
            nsr.setOjeeRank(nsr.getRank().toString());
        }
        nsr.setAdmissionDate(Date.valueOf(LocalDate.now()));
            return mapperService.convertToNSRDto(nsrRepository.save(nsr));
        }else {
            throw new RecordAlreadyExistsException("Record already exists");
        }
    }

    @Override
    public void bulkSaveNSRData(Set<NSR> nsrs){
        Set<NSR> savedNSRs = (Set<NSR>) nsrRepository.saveAll(nsrs);
    }

    @Override
    public NSRDto postNSRDataByStudent(NSR nsr){
        logger.info("Posting NSR data for student "+nsr.getJeeApplicationNo());
        return mapperService.convertToNSRDto(nsrRepository.save(nsr));
    }

    @Override
    @Cacheable(value = "nsr", key = "#rollNo")
    public NSRDto getNSRDataByRollNo(String rollNo) {
        logger.info("Fetching NSR data for roll no. "+rollNo);
        return mapperService.convertToNSRDto(nsrRepository.findById(rollNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }
    

    @Override
    public List<NSRDto> getAllNSRData() {
        List<NSR> nsr = StreamSupport.stream(nsrRepository.findAll().spliterator(), false).toList();
        List<NSRDto> nsrDtos = new ArrayList<>();
        nsr.forEach(nsr1 -> nsrDtos.add(mapperService.convertToNSRDto(nsr1)));
        return nsrDtos;
    }

    @Override
    public NSRDto getNSRDataByJeeApplicationNo(String jeeApplicationNo) {
        return mapperService.convertToNSRDto(nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found")));
    }

    @Transactional
    @Override
    public Boolean saveToPermanentDatabase(String jeeApplicationNo){
        SharedStateAmongDueInitiationAndNSRService sharedState = new SharedStateAmongDueInitiationAndNSRService();
        NSR nsr = nsrRepository.findById(jeeApplicationNo).orElseThrow(() -> new RecordNotFoundException("Record not found"));
        nsr.setBatchId(nsr.getCourse().getEnumName() + nsr.getAdmissionYear() + nsr.getBranchCode() + nsr.getStudentType());
        nsr.setCurrentYear(((nsr.getStudentType().equals(StudentType.REGULAR))?1:2));
        logger.info("Batch ID : {}",nsr.getBatchId());
        logger.info("Fetched from Redis");
        CompletableFuture<Boolean> processDues = duesInitiationServiceImpl.initiateDues(new DuesDetailsInitiationDTO(nsr),sharedState);
        try{
            logger.info("Fetching from Redis");
            Student student = mapperService.convertToStudent(nsr);
            student.setHostelier(nsr.getHostelOption());
            student.setTransportAvailed(nsr.getTransportOpted());

            logger.info("NSR object : {}",nsr);
            StudentAdmissionDetails studentAdmissionDetails = mapperService.convertToStudentAdmissionDetails(nsr);
            StudentCareer studentCareer = mapperService.convertToStudentCareer(nsr);
            PersonalDetails personalDetails = mapperService.convertToPersonalDetails(nsr);
            List<StudentDocs> studentDocs1;
            if(nsr.getStudentDocsData()!=null){
                logger.info("Student Docs : {}",nsr.getStudentDocsData());
                 studentDocs1 = (mapperService.convertToStudentDocs(nsr.getStudentDocsData())).stream()
                         .map(studentDocs2 ->
                         {
                             studentDocs2.setStudent(student);
                             return studentDocs2;
                         }).collect(Collectors.toList());
            }
            else{
                studentDocs1 = null;
            }
            studentAdmissionDetails.setReportingDate(Date.valueOf(LocalDate.now()));
            logger.info("Student Docs in databse entity format {}" , studentDocs1);
            student.setStudentAdmissionDetails(studentAdmissionDetails);
            studentAdmissionDetails.setStudent(student);
            student.setStudentCareer(studentCareer);
            studentCareer.setStudent(student);
            personalDetails.setStudent(student);
            student.setPersonalDetails(personalDetails);
            student.setStudentDocs(studentDocs1);
            Transport transport = mapperService.convertToTransport(nsr);
            transport.setRegdYear(Year.now().getValue());
            Hostel hostel = mapperService.convertToHostel(nsr);
            if(nsr.getTransportOpted().equals(BooleanString.YES)){
                transport.setTransportAvailed(BooleanString.NO);
                transport.setRoute("N/A");
            }
            else {
                transport.setTransportAvailed(BooleanString.NO);
                transport.setPickUpPoint("N/A");
                transport.setRoute("N/A");

            }
            hostel.setRegdyear(nsr.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
            transport.setRegdYear(nsr.getStudentType().equals(StudentType.REGULAR) ? 1 : 2);
            student.setTransport(transport);
            transport.setStudent(student);
            hostel.setHostelier(BooleanString.NO);
            if(hostel.getHostelOption().equals(BooleanString.NO)){
                hostel.setHostelChoice(HostelChoice.NONE);
            }
            student.setHostel(hostel);
            hostel.setStudent(student);
            logger.info("Student object : {}",student);
            logger.info("Started saving to databse");
            logger.info("Student before saving : {} ",student);
            studentRepository.saveAndFlush(student);
            logger.info("Saved to database");
            processDues.join();
            String password = generateRandomPassword();
            try{
                logger.info("Inside try block to process user creation");
                String response = userCreationService.createUser(
                        student.getStudentName(),
                        "STUDENT",
                        student.getBranchCode(),
                        student.getRegdNo(),
                        password,
                        student.getEmail(),
                        student.getDegreeYop());
                logger.info("Response for Microsoft : {}",response);
//                userCreationService.setProfilePicture(nsr.getRegdNo(), response);
                emailSenderServiceImpl.sendTridentCredentialsEmail(response,password);
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }catch (Exception e){
            sharedState.setProceed(false);
            return false;
        }
    }

    @Override
    public Set<NSRDto> getAllNSRDataByAdmissionYear(String admissionYear) {
        logger.info(nsrRepository.findAllByAdmissionYear(admissionYear).toString());
        return mapperService.convertToNSRDtoSet(nsrRepository.findAllByAdmissionYear(admissionYear));
    }



    public String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return password.toString();
    }
}
