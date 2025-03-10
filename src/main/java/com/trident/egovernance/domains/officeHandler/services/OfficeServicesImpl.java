package com.trident.egovernance.domains.officeHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.helpers.*;
import com.trident.egovernance.global.repositories.permanentDB.*;
import com.trident.egovernance.global.services.MapperService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class OfficeServicesImpl implements OfficeServices {
    private final Logger logger = LoggerFactory.getLogger(OfficeServicesImpl.class);
    private final StudentRepository studentRepository;
    private final MapperService mapperService;
    private final PersonalDetailsRepository personalDetailsRepository;
    private final TransportRepository transportRepository;
    private final HostelRepository hostelRepository;
    private final StudentAdmissionDetailsRepository studentAdmissionDetailsRepository;
    private final StudentCareerRepository studentCareerRepository;
    private final StudentDocsRepository studentDocsRepository;
    private final SectionsRepository sectionsRepository;
    private EntityManager entityManager;
    private final BranchRepository branchRepository;
    private final SessionsRepository sessionsRepository;

    public OfficeServicesImpl(StudentRepository studentRepository, MapperService mapperService, PersonalDetailsRepository personalDetailsRepository, TransportRepository transportRepository, HostelRepository hostelRepository, StudentAdmissionDetailsRepository studentAdmissionDetailsRepository, StudentCareerRepository studentCareerRepository, StudentDocsRepository studentDocsRepository,
                              BranchRepository branchRepository, SessionsRepository sessionsRepository, SectionsRepository sectionsRepository) {
        this.studentRepository = studentRepository;
        this.mapperService = mapperService;
        this.personalDetailsRepository = personalDetailsRepository;
        this.transportRepository = transportRepository;
        this.hostelRepository = hostelRepository;
        this.studentAdmissionDetailsRepository = studentAdmissionDetailsRepository;
        this.studentCareerRepository = studentCareerRepository;
        this.studentDocsRepository = studentDocsRepository;
        this.branchRepository = branchRepository;
        this.sessionsRepository = sessionsRepository;
        this.sectionsRepository = sectionsRepository;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) { // Use setter for injection
        this.entityManager = entityManager;
    }

    public List<StudentOfficeDTO> getAllContinuingStudents() {
        return mapperService.convertToStudentOfficeDTO(studentRepository.findAllByStatusAlongWithParentContact(StudentStatus.CONTINUING));
    }

    public List<StudentOfficeDTO> getAllAlumniStudents() {
        return mapperService.convertToStudentOfficeDTO(studentRepository.findAllByStatusAlongWithParentContact(StudentStatus.ALUMNI));
    }

    public Long countAllContinuingStudents() {
        return studentRepository.countAllByStatus(StudentStatus.CONTINUING);
    }

    public Long countAllAlumningStudents() {
        return studentRepository.countAllByStatus(StudentStatus.ALUMNI);
    }

    public List<CourseStudentCountDTO> getGroupedStudentsCount(String status) {
        List<Object[]> rawCounts = studentRepository.findRawStudentCountsGroupedByCourseAndBranch(StudentType.REGULAR.name(), status);
        Map<Courses, List<BranchCountDTO>> courseBranchMap = new HashMap<>();
        Map<Courses, Long> courseStudentCountMap = new HashMap<>();

        for (Object[] row : rawCounts) {
            String course = (String) row[0];
            String branchCode = (String) row[1];
            long studentCount = ((Number) row[2]).longValue(); // Ensure correct type casting

            Courses courseEnum = Courses.fromDisplayName(course); // Assuming `Courses` is an enum

            // Update or initialize the list of BranchCountDTO for the current course
            courseBranchMap.computeIfAbsent(courseEnum, k -> new ArrayList<>())
                    .add(new BranchCountDTO(branchCode, studentCount));

            // Accumulate total student count for the course
            courseStudentCountMap.put(courseEnum,
                    courseStudentCountMap.getOrDefault(courseEnum, 0L) + studentCount);
        }

        // Now convert the data into a list of CourseStudentCountDTO
        List<CourseStudentCountDTO> result = new ArrayList<>();
        for (Courses courseEnum : courseBranchMap.keySet()) {
            List<BranchCountDTO> branches = courseBranchMap.get(courseEnum);
            long totalStudentCount = courseStudentCountMap.getOrDefault(courseEnum, 0L);
            result.add(new CourseStudentCountDTO(courseEnum, totalStudentCount, branches));
        }

        return result;
    }

    public StudentIndividualRecordFetchDTO getStudentByRegdNo(String regdNo) {
        try {
            logger.info("Fetching student details for registration number: {}", regdNo);

            // Fetch the student or throw an exception if not found
            Student student = studentRepository.findByRegdNoComplete(regdNo)
                    .orElseThrow(() -> new RecordNotFoundException("Student Not Found"));

            // Map student documents to DTOs
            List<StudentDocsOnlyDTO> studentDocsDTOS = student.getStudentDocs().stream()
                    .map(StudentDocsOnlyDTO::new)
                    .toList();

            // Build and return the full DTO
            return new StudentIndividualRecordFetchDTO(
                    new StudentOnlyDTO(student),
                    new PersonalDetailsOnlyDTO(student.getPersonalDetails()),
                    new StudentAdmissionDetailsOnlyDTO(student.getStudentAdmissionDetails()),
                    new StudentCareerOnlyDTO(student.getStudentCareer()),
                    new HostelOnlyDTO(student.getHostel()),
                    new TransportOnlyDTO(student.getTransport()),
                    studentDocsDTOS
            );
        } catch (RecordNotFoundException e) {
            logger.error("Student not found for registration number: {}", regdNo);
            throw e; // Re-throwing the same exception without wrapping it
        } catch (NullPointerException e) {
            logger.error("Student details are not properly initialized for registration number: {}", regdNo, e);
            throw new RecordNotFoundException("All details not properly initiated");
        }
    }

    @Transactional
    public Boolean updateStudentTableOnly(StudentOnlyDTO updatedStudent, String regdNo) {
        try {
            logger.info(regdNo);
            if (studentRepository.updateStudent(
                    updatedStudent.studentName(),
                    updatedStudent.gender(),
                    updatedStudent.dob(),
                    updatedStudent.course(),
                    updatedStudent.branchCode(),
                    updatedStudent.admissionYear(),
                    updatedStudent.degreeYop(),
                    updatedStudent.phNo(),
                    updatedStudent.email(),
                    updatedStudent.studentType(),
                    updatedStudent.hostelier(),
                    updatedStudent.transportAvailed(),
                    updatedStudent.status(),
                    updatedStudent.batchId(),
                    updatedStudent.currentYear(),
                    updatedStudent.aadhaarNo(),
                    updatedStudent.indortrng(),
                    updatedStudent.plpoolm(),
                    updatedStudent.cfPayMode(),
                    updatedStudent.religion(),
//                    updatedStudent.section(),
                    regdNo
            ) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException || e instanceof ConstraintViolationException || e instanceof SQLException) {
                logger.error(e.getMessage());
                throw new InvalidInputsException("Invalid data Inputs");
            } else {
                throw new RuntimeException("Unexpected Error Occured");
            }
        }
    }

    @Transactional
    public Boolean updateStudentCareerTable(StudentCareerOnlyDTO studentCareerOnlyDTO, String regdNo) {
        try {
            if (studentCareerRepository.updateStudentCareer(
                    studentCareerOnlyDTO.tenthPercentage(),
                    studentCareerOnlyDTO.tenthYOP(),
                    studentCareerOnlyDTO.twelvthPercentage(),
                    studentCareerOnlyDTO.twelvthYOP(),
                    studentCareerOnlyDTO.diplomaPercentage(),
                    studentCareerOnlyDTO.diplomaYOP(),
                    studentCareerOnlyDTO.graduationPercentage(),
                    studentCareerOnlyDTO.graduationYOP(),
                    regdNo
            ) == 1) {
                return true;
            } else {
                throw new RecordNotFoundException("Student Not Found");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid data Inputs");
        }
    }

    @Transactional
    public Boolean updatePersonalDetailsTable(PersonalDetailsOnlyDTO dto, String regdNo) {
        try {
            if (personalDetailsRepository.updatePersonalDetails(
                    dto.fname(),
                    dto.mname(),
                    dto.lgName(),
                    dto.permanentAddress(),
                    dto.permanentCity(),
                    dto.permanentState(),
                    dto.permanentPincode(),
                    dto.parentContact(),
                    dto.parentEmailId(),
                    dto.presentAddress(),
                    dto.district(),
                    regdNo
            ) == 1) {
                return true;
            } else {
                throw new RecordNotFoundException("Student Not Found");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid data Inputs");
        }
    }

    @Transactional
    public Boolean updateStudentAdmissionDetailsTable(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsDTO, String regdNo) {
        try {
            if (studentAdmissionDetailsRepository.updateStudentAdmissionDetails(
                    studentAdmissionDetailsDTO.admissionDate(),
                    studentAdmissionDetailsDTO.ojeeCounsellingFeePaid(),
                    studentAdmissionDetailsDTO.tfw(),
                    studentAdmissionDetailsDTO.admissionType(),
                    studentAdmissionDetailsDTO.ojeeRollNo(),
                    studentAdmissionDetailsDTO.ojeeRank(),
                    studentAdmissionDetailsDTO.aieeeRank(),
                    studentAdmissionDetailsDTO.caste(),
                    studentAdmissionDetailsDTO.reportingDate(),
                    studentAdmissionDetailsDTO.categoryCode(),
                    studentAdmissionDetailsDTO.categoryRank(),
                    studentAdmissionDetailsDTO.jeeApplicationNo(),
                    studentAdmissionDetailsDTO.allotmentId(),
                    regdNo
            ) == 1) {
                return true;
            } else {
                throw new RecordNotFoundException("Student Not Found");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid data Inputs");
        }
    }

    @Transactional
    public Boolean updateHostelTable(HostelOnlyDTO studentHostelDetailsDTO, String regdNo) {
        try {
            if (hostelRepository.updateStudentHostelDetails(
                    studentHostelDetailsDTO.hostelier(),
                    studentHostelDetailsDTO.hostelOption(),
                    studentHostelDetailsDTO.hostelChoice(),
                    studentHostelDetailsDTO.lgName(),
                    studentHostelDetailsDTO.regdyear(),
                    regdNo
            ) == 1) {
                return true;
            } else {
                throw new RecordNotFoundException("Student Not Found");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid data Inputs");
        }
    }

    @Transactional
    public Boolean updateTransportTable(TransportOnlyDTO transportOnlyDTO, String regdNo) {
        try {
            if (transportRepository.updateTransportDetails(
                    transportOnlyDTO.transportAvailed(),
                    transportOnlyDTO.transportOpted(),
                    transportOnlyDTO.route(),
                    transportOnlyDTO.pickUpPoint(),
                    transportOnlyDTO.regdYear(),
                    regdNo
            ) == 1) {
                return true;
            } else {
                throw new RecordNotFoundException("Student Not Found");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InvalidInputsException("Invalid data Inputs");
        }
    }

//    @Transactional
//    public Boolean updateStudentDocsTable(List<StudentDocsOnlyDTO> updates, String regdNo) {
//        logger.info(updates.toString());

    /// /        List<StudentDocs> studentDocs = new ArrayList<>();
    /// /        for (StudentDocsOnlyDTO dto : studentDocsOnlyDTO) {
    /// /            StudentDocs entity = new StudentDocs(dto);
    /// /            studentDocs.add(entity);
    /// /        }
    /// /
    /// /        // Merge entities and collect the merged results
    /// /        List<StudentDocs> mergedDocs = new ArrayList<>();
    /// /        for (StudentDocs doc : studentDocs) {
    /// /            mergedDocs.add(entityManager.merge(doc));
    /// /        }
    /// /        // Convert merged entities back to DTOs and return
    /// /        return mergedDocs.stream()
    /// /                .map(StudentDocsOnlyDTO::new)
    /// /                .toList();
//
//        if (updates == null || updates.isEmpty()) {
//            return false;
//        }
//
//        try{
//            StringBuilder queryBuilder = new StringBuilder("UPDATE STUDENTDOCS s SET ");
//
//            // Setting the fields to update using CASE statements
//            queryBuilder.append("s.uploadDate = CASE ");
//            for (int i = 0; i < updates.size(); i++) {
//                queryBuilder.append("WHEN s.regdNo = :regdNo").append(i)
//                        .append(" AND s.docId = :docId").append(i).append(" THEN :uploadDate").append(i).append(" ");
//            }
//            queryBuilder.append("END ");
//
//            // Adding the WHERE clause
//            queryBuilder.append("WHERE (s.regdNo, s.docId) IN (");
//            for (int i = 0; i < updates.size(); i++) {
//                queryBuilder.append("(:regdNo").append(i).append(", :docId").append(i).append(")");
//                if (i < updates.size() - 1) {
//                    queryBuilder.append(", ");
//                }
//            }
//            queryBuilder.append(")");
//
//            // Create the query and set parameters
//            var query = entityManager.createNativeQuery(queryBuilder.toString());
//
//            // Set parameters to avoid SQL injection
//            for (int i = 0; i < updates.size(); i++) {
//                query.setParameter("regdNo" + i, regdNo);
//                query.setParameter("docId" + i, updates.get(i).docId());
//                query.setParameter("uploadDate" + i, new java.sql.Date(updates.get(i).uploadDate().getTime()));
//            }
//            // Execute the update
//            query.executeUpdate();
//            return true;
//        }catch (Exception e){
//            logger.error(e.getMessage());
//            throw new InvalidInputsException("Invalid Inputs");
//        }
//    }
    @Transactional
    public Boolean updateStudentDocsTable(List<StudentDocsOnlyDTO> updates, String regdNo) {
        try {
            List<StudentDocs> studentDocs = new ArrayList<>();
            for (StudentDocsOnlyDTO dto : updates) {
                StudentDocs studentDoc = new StudentDocs(dto);
                studentDoc.setStudent(entityManager.getReference(Student.class, regdNo));
                studentDocs.add(studentDoc);
            }
            studentDocsRepository.saveAll(studentDocs);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Boolean addDocsToStudentDocsTable(List<StudentDocsOnlyDTO> updates, String regdNo) {
        logger.info(updates.toString());
        try {
            Set<StudentDocs> studentDocsSet = new HashSet<>();
            if (studentRepository.existsById(regdNo)) {
                for (StudentDocsOnlyDTO studentDocsOnlyDTO : updates) {
                    StudentDocs studentDocs = new StudentDocs(studentDocsOnlyDTO);
                    studentDocs.setStudent(entityManager.getReference(Student.class, regdNo));
                    studentDocsSet.add(studentDocs);
                }
                studentDocsRepository.saveAll(studentDocsSet);
                return true;
            }
            throw new RecordNotFoundException("Student Not Found");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public List<AdmissionData> getAdmissionData(Optional<String> admissionYear) {
        return studentRepository.getStudentSummary(Gender.MALE, Gender.FEMALE, Religion.HINDU, TFWType.TFW, TFWType.NTFW, admissionYear.orElse(null));
    }

    public List<TotalAdmissionData> getTotalAdmissionData(Optional<Courses> course, Optional<String> branch) {
        return studentRepository.getAdmissionSummaryByCourseAndBranch(course.orElse(null), branch.orElse(null));
    }

    public List<SessionWiseRecords> getSessionWiseRecords(Optional<StudentStatus> status) {
        return studentRepository.fetchSessionWiseStatistics(status.orElse(null));
    }

    @Override
    public List<StudentBasicDTO>  fetchStudentDataWithRollSheet(Courses course, String branch, Integer currentYear) {
        return studentRepository.findStudentWithCourseAndCurrentYearAndBranchCode(course,currentYear, branch);
    }

    @Override
    @Transactional
    public Boolean initializeSection(SectionFetcher sectionFetcher, String mode) {
        Long sectionId;
        if(mode.equals("create")){
            sectionId = sectionsRepository.getMaxId();
        }
        else if(mode.equals("update")){
            logger.info("Updating section");
            sectionId = sectionsRepository.findSectionIdByCourseAndSemAndBranchCodeAndSection(sectionFetcher.course().getDisplayName(), sectionFetcher.sem(), sectionFetcher.branchCode(), sectionFetcher.section()).orElseThrow(() -> new RecordNotFoundException("Section Not Found"));
        }
        else{
            throw new InvalidInputsException("Invalid mode");
        }
        Sections sections = new Sections(sectionFetcher);
        sections.setSectionId(sectionId);

        // Set Roll_Sheet entities
        List<Roll_Sheet> newStudentSectionData = new ArrayList<>();
        for (StudentSectionData studentSectionData : sectionFetcher.studentSectionData()) {
            Roll_Sheet rollSheet = new Roll_Sheet(studentSectionData);
            rollSheet.setSections(sections);  // Maintain bidirectional relationship
            newStudentSectionData.add(rollSheet);
        }
        sections.setRollSheets(newStudentSectionData);

        // Log and Save
//        logger.info("Saving section: {}", sections);
        sectionsRepository.saveAndFlush(sections);
        return true;
    }

    @Override
    public SectionFetcher getSectionList(String course, Integer sem, String branchCode, String section) {
        Sections sections = sectionsRepository.findAllByCourseAndSemAndBranchCodeAndSection(course, sem, branchCode, section).orElseThrow(() -> new RecordNotFoundException("Section Not Found"));
        List<Roll_Sheet> rollSheets = sections.getRollSheets();
        StudentSectionData sectionData;
        List<StudentSectionData> studentSectionDataList = new ArrayList<>();
        for (Roll_Sheet roll_sheet : rollSheets) {
            sectionData = new StudentSectionData(roll_sheet);
            studentSectionDataList.add(sectionData);
        }
        return new SectionFetcher(sections, studentSectionDataList);
    }


//    public boolean createSection(Sections sections){
//        sectionsRepository.save(sections);
//        Student student= new Student();
//        student.setSec
//    }


}
