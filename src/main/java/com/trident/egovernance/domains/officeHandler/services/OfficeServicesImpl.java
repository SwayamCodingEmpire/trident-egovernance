package com.trident.egovernance.domains.officeHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.*;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.services.MapperService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfficeServicesImpl {
    private final Logger logger = LoggerFactory.getLogger(OfficeServicesImpl.class);
    private final StudentRepository studentRepository;
    private final MapperService mapperService;
    private EntityManager entityManager;

    public OfficeServicesImpl(StudentRepository studentRepository, MapperService mapperService) {
        this.studentRepository = studentRepository;
        this.mapperService = mapperService;

    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) { // Use setter for injection
        this.entityManager = entityManager;
    }

    public List<StudentOfficeDTO> getAllContinuingStudents(){
        return studentRepository.findAllByStatusAlongWithParentContact(StudentStatus.CONTINUING);
    }

    public List<StudentOfficeDTO> getAllAlumniStudents(){
        return studentRepository.findAllByStatusAlongWithParentContact(StudentStatus.ALUMNI);
    }

    public Long countAllContinuingStudents(){
        return studentRepository.countAllByStatus(StudentStatus.CONTINUING);
    }

    public Long countAllAlumningStudents(){
        return studentRepository.countAllByStatus(StudentStatus.ALUMNI);
    }

    public List<CourseStudentCountDTO> getGroupedStudentsCount(String status){
        List<Object[]> rawCounts = studentRepository.findRawStudentCountsGroupedByCourseAndBranch(StudentType.REGULAR.name(),status);
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
        try
        {
            Student student = studentRepository.findByRegdNoComplete(regdNo).orElseThrow(() -> new RecordNotFoundException("Student Not Found"));
            List<StudentDocsOnlyDTO> studentOnlyDTOS = new ArrayList<>();
            for (StudentDocs studentDocs1 : student.getStudentDocs()) {
                StudentDocsOnlyDTO studentOnlyDTO = new StudentDocsOnlyDTO(studentDocs1);
                studentOnlyDTOS.add(studentOnlyDTO);
            }
            return new StudentIndividualRecordFetchDTO(
                    new StudentOnlyDTO(student),
                    new PersonalDetailsOnlyDTO(student.getPersonalDetails()),
                    new StudentAdmissionDetailsOnlyDTO(student.getStudentAdmissionDetails()),
                    new StudentCareerOnlyDTO(student.getStudentCareer()),
                    new HostelOnlyDTO(student.getHostel()),
                    new TransportOnlyDTO(student.getTransport()),
                    studentOnlyDTOS
            );
        }
        catch (NullPointerException e){
            throw new RecordNotFoundException("All Details not properly initiated");
        }
        catch (RecordNotFoundException e){
            throw new RecordNotFoundException("Student Not Found");
        }
    }

    @Transactional
    public StudentOnlyDTO updateStudentTableOnly(StudentOnlyDTO studentOnlyDTO) {
//        Student updatedStudent = mapperService.convertToStudentFromStudentOnlyDTO(studentOnlyDTO);
//        updatedStudent.setStudentCareer(entityManager.getReference(StudentCareer.class,studentOnlyDTO.regdNo()));
//        updatedStudent.setStudentAdmissionDetails(entityManager.getReference(StudentAdmissionDetails.class,studentOnlyDTO.regdNo()));
//        updatedStudent.setPersonalDetails(entityManager.getReference(PersonalDetails.class,studentOnlyDTO.regdNo()));
//        updatedStudent.setHostel(entityManager.getReference(Hostel.class,studentOnlyDTO.regdNo()));
//        updatedStudent.setTransport(entityManager.getReference(Transport.class,studentOnlyDTO.regdNo()));
        return new StudentOnlyDTO(entityManager.merge(new Student(studentOnlyDTO)));
    }

    @Transactional
    public StudentCareerOnlyDTO updateStudentCareerTable(StudentCareerOnlyDTO studentCareerOnlyDTO) {
        return new StudentCareerOnlyDTO(entityManager.merge(new StudentCareer(studentCareerOnlyDTO)));
    }

    @Transactional
    public PersonalDetailsOnlyDTO updatePersonalDetailsTable(PersonalDetailsOnlyDTO personalDetailsOnlyDTO) {
        return new PersonalDetailsOnlyDTO(entityManager.merge(new PersonalDetails(personalDetailsOnlyDTO)));
    }

    @Transactional
    public StudentAdmissionDetailsOnlyDTO updateStudentAdmissionDetailsTable(StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsOnlyDTO) {
        return new StudentAdmissionDetailsOnlyDTO(entityManager.merge(new StudentAdmissionDetails(studentAdmissionDetailsOnlyDTO)));
    }

    @Transactional
    public HostelOnlyDTO updateHostelTable(HostelOnlyDTO hostelOnlyDTO) {
        logger.info(hostelOnlyDTO.toString());
        Hostel hostel = new Hostel(hostelOnlyDTO);
        logger.info(hostel.toString());
        return new HostelOnlyDTO(entityManager.merge(new Hostel(hostelOnlyDTO)));
    }
    @Transactional
    public TransportOnlyDTO updateTransportTable(TransportOnlyDTO transportOnlyDTO) {
        return new TransportOnlyDTO(entityManager.merge(new Transport(transportOnlyDTO)));
    }

    @Transactional
    public List<StudentDocsOnlyDTO> updateStudentDocsTable(List<StudentDocsOnlyDTO> studentDocsOnlyDTO) {
        List<StudentDocs> studentDocs = new ArrayList<>();
        for (StudentDocsOnlyDTO dto : studentDocsOnlyDTO) {
            StudentDocs entity = new StudentDocs(dto);
            studentDocs.add(entity);
        }

        // Merge entities and collect the merged results
        List<StudentDocs> mergedDocs = new ArrayList<>();
        for (StudentDocs doc : studentDocs) {
            mergedDocs.add(entityManager.merge(doc));
        }
        // Convert merged entities back to DTOs and return
        return mergedDocs.stream()
                .map(StudentDocsOnlyDTO::new)
                .toList();
    }
}
