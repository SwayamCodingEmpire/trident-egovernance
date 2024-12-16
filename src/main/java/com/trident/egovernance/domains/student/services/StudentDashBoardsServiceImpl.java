package com.trident.egovernance.domains.student.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.entities.views.Results;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.views.AttendanceRepository;
import com.trident.egovernance.global.repositories.views.ResultsRepository;
import com.trident.egovernance.global.services.MiscellaniousServices;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StudentDashBoardsServiceImpl implements StudentDashBoardsService {
    private final Logger logger = LoggerFactory.getLogger(StudentDashBoardsServiceImpl.class);
    private final DuesDetailsRepository duesDetailsRepository;
    private final MiscellaniousServices miscellaniousServices;
    private final StudentRepository studentRepository;
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final ExecutorService executorService;
    private final ResultsRepository resultsRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentDashBoardsServiceImpl(DuesDetailsRepository duesDetailsRepository, MiscellaniousServices miscellaniousServices, StudentRepository studentRepository, UserDataFetcherFromMS userDataFetcherFromMS, ResultsRepository resultsRepository, AttendanceRepository attendanceRepository) {
        this.duesDetailsRepository = duesDetailsRepository;
        this.miscellaniousServices = miscellaniousServices;
        this.studentRepository = studentRepository;
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.resultsRepository = resultsRepository;

        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.attendanceRepository = attendanceRepository;
    }


    public StudentProfileDTO getStudentProfile(UserJobInformationDto userJobInformationDto) {
        // Fetch user job information using the username from the claims

        // Fetch the student profile using employee ID
        Student student = studentRepository.findStudentProfileData(userJobInformationDto.employeeId());
        logger.info(student.toString());
        // Return the student profile DTO
        return new StudentProfileDTO(student, userJobInformationDto);

    }

    public void testQueryForStudentDashboard(String regdNo){
        Student student = studentRepository.findStudentProfileData(regdNo);
        logger.info(student.toString());
    }

    public StudentDuesDetails getStudentDuesDetails() {
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        CompletableFuture<Set<DuesDetailsDto>> duesDetails = CompletableFuture.supplyAsync(
                () -> duesDetailsRepository.findAllByRegdNo(userJobInformationDto.employeeId()), executorService
        );
        CompletableFuture<PaymentDuesDetails> dueDetailsSummary = CompletableFuture.supplyAsync(
                () -> duesDetailsRepository.findSummaryByRegdNo(userJobInformationDto.employeeId()), executorService
        );
        CompletableFuture<StudentDuesDetails> studentDuesDetailsCompletableFuture =  CompletableFuture.allOf(duesDetails, dueDetailsSummary)
                .thenApply(voidResult -> {
                    try {
                        return new StudentDuesDetails(duesDetails.join(), dueDetailsSummary.join());
                    }catch (Exception e){
                        logger.error("Error while fetching student dues details", e);
                        throw new RecordNotFoundException("Record Not Found");
                    }
                });
        return studentDuesDetailsCompletableFuture.join();
    }

    public SemesterResultData getSemesterResults(){
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        CompletableFuture<SGPADTO> maxSGPAs = CompletableFuture.supplyAsync(
                () -> {
                    SGPADTO result = resultsRepository.findMaxSgpasByCourseAndYear(userJobInformationDto.employeeId());
                    return result != null ? result : new SGPADTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO); // Default to zero if null
                }, executorService
        );
        CompletableFuture<SGPADTO> avgSGPAs = CompletableFuture.supplyAsync(
                () -> {
                    SGPADTO result = resultsRepository.findAvgSgpasByCourseAndYear(userJobInformationDto.employeeId());
                    return result != null ? result : new SGPADTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO); // Default to zero if null
                }, executorService
        );
        CompletableFuture<SGPADTO> studentSGPASGPAs = CompletableFuture.supplyAsync(
                () -> resultsRepository.findByRegdNo(userJobInformationDto.employeeId()).orElse(new SGPADTO(BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO)), executorService
        );
        CompletableFuture<SemesterResultData> semesterResults = CompletableFuture.allOf(maxSGPAs, avgSGPAs, studentSGPASGPAs)
                .thenApply(result -> new SemesterResultData(maxSGPAs.join(), avgSGPAs.join(), studentSGPASGPAs.join()));
        return semesterResults.join();
    }

    public List<AttendanceSummaryDTO> getAttendanceSummary(){
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        return attendanceRepository.findAllByRegdNo(userJobInformationDto.employeeId());
    }
}
