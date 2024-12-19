package com.trident.egovernance.domains.student.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.repositories.permanentDB.DuesDetailsRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentCareerRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.views.AttendanceRepository;
import com.trident.egovernance.global.repositories.views.ResultsRepository;
import com.trident.egovernance.global.repositories.views.SemesterResultRepository;
import com.trident.egovernance.global.repositories.views.StudAttViewRepository;
import com.trident.egovernance.global.services.MapperService;
import com.trident.egovernance.global.services.MiscellaniousServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class StudentDashBoardsServiceImpl implements StudentDashBoardsService {
    private final MapperService mapperService;
    private final Logger logger = LoggerFactory.getLogger(StudentDashBoardsServiceImpl.class);
    private final DuesDetailsRepository duesDetailsRepository;
    private final MiscellaniousServices miscellaniousServices;
    private final StudentRepository studentRepository;
    private final ExecutorService executorService;
    private final ResultsRepository resultsRepository;
    private final AttendanceRepository attendanceRepository;
    private final SemesterResultRepository semesterResultRepository;
    private final StudentCareerRepository studentCareerRepository;
    private final StudAttViewRepository studAttViewRepository;

    public StudentDashBoardsServiceImpl(MapperService mapperService, DuesDetailsRepository duesDetailsRepository, MiscellaniousServices miscellaniousServices, StudentRepository studentRepository, ResultsRepository resultsRepository, AttendanceRepository attendanceRepository, SemesterResultRepository semesterResultRepository, StudentCareerRepository studentCareerRepository, StudAttViewRepository studAttViewRepository) {
        this.mapperService = mapperService;
        this.duesDetailsRepository = duesDetailsRepository;
        this.miscellaniousServices = miscellaniousServices;
        this.studentRepository = studentRepository;
        this.resultsRepository = resultsRepository;
        this.semesterResultRepository = semesterResultRepository;
        this.studentCareerRepository = studentCareerRepository;

        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.attendanceRepository = attendanceRepository;
        this.studAttViewRepository = studAttViewRepository;
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

    public SemesterResultData getSemesterResultsSortedByCourse(){
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

    public SemesterResultData getSemesterResultsSortedByBranch(){
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        CompletableFuture<SGPADTO> maxSGPAs = CompletableFuture.supplyAsync(
                () -> {
                    SGPADTO result = resultsRepository.findMaxSgpasByBranchAndYear(userJobInformationDto.employeeId());
                    return result != null ? result : new SGPADTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO); // Default to zero if null
                }, executorService
        );
        CompletableFuture<SGPADTO> avgSGPAs = CompletableFuture.supplyAsync(
                () -> {
                    SGPADTO result = resultsRepository.findAvgSgpasByBranchAndYear(userJobInformationDto.employeeId());
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


    public Map<Integer, List<AttendanceSummaryDTO>> getAttendanceSummary() {
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        List<AttendanceSummaryDTO> attendanceSummaryDTOS = studAttViewRepository.findAllByRegdNo(userJobInformationDto.employeeId());
        logger.info(attendanceSummaryDTOS.toString());
        return attendanceSummaryDTOS.stream()
                .collect(Collectors.groupingBy(
                        AttendanceSummaryDTO::sem
                ));
    }


    public StudentSubjectWiseResults getResultsGroupedBySemester() {
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        // Fetch results from the repository
        List<SubjectResultData> semesterResults = mapperService.convertToSubjectResultsData(semesterResultRepository.findAllByRegdNo(userJobInformationDto.employeeId()));

        // Group and transform results
        Map<Integer, List<SubjectResultData>> groupedResults = semesterResults.stream()
                .filter(result -> !result.subjectName().equals("N/A"))
                .collect(Collectors.groupingBy(SubjectResultData::semester));  // Group by semester directly

        // Wrap the grouped results in a SubjectWiseResults record
        return new StudentSubjectWiseResults(groupedResults);
    }

    public List<StudentCareerHistory> getStudentCareerDTO(){
        UserJobInformationDto userJobInformationDto = miscellaniousServices.getUserJobInformation();
        StudentCareerOnlyDTO studentCareerOnlyDTO = studentCareerRepository.findByRegdNo(userJobInformationDto.employeeId());
        CompletableFuture<BigDecimal> cgpa = CompletableFuture.supplyAsync(
                () -> resultsRepository.findCGPAByRegdNo(userJobInformationDto.employeeId()).orElse(BigDecimal.ZERO), executorService
        );
        List<StudentCareerHistory> studentCareerHistories = new ArrayList<>();
        if(studentCareerOnlyDTO.tenthPercentage() != null){
            studentCareerHistories.add(new StudentCareerHistory("10th", studentCareerOnlyDTO.tenthPercentage()));
        }
        if(studentCareerOnlyDTO.twelvthPercentage() != null){
            studentCareerHistories.add(new StudentCareerHistory("12th", studentCareerOnlyDTO.twelvthPercentage()));
        }
        if(studentCareerOnlyDTO.diplomaPercentage() != null){
            studentCareerHistories.add((new StudentCareerHistory("Diploma", studentCareerOnlyDTO.diplomaPercentage())));
        }
        if(studentCareerOnlyDTO.graduationPercentage() != null){
            studentCareerHistories.add(new StudentCareerHistory("Graduation", studentCareerOnlyDTO.graduationPercentage()));
        }
        BigDecimal CGPA = cgpa.join();
        studentCareerHistories.add(new StudentCareerHistory("Current", CGPA));
        return studentCareerHistories;
    }
}
