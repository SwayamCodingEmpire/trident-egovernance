package com.trident.egovernance.domains.officeHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.permanentDB.Branch;
import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.entities.permanentDB.StudentDocs;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.helpers.StudentType;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfficeServicesImpl {
    private final StudentRepository studentRepository;

    public OfficeServicesImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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
}
