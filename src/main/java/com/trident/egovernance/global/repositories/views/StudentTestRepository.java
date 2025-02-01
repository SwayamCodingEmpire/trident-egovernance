package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.Student_Test;
import com.trident.egovernance.global.helpers.Courses;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StudentTestRepository extends JpaRepository<Student_Test, String> {
    @Query("SELECT s FROM STUDENT_TEST s WHERE s.course = :course AND s.branchCode = :branch AND s.regdNo != :regdNo")
    List<Student_Test> findAllByCourse(String regdNo, Courses course, String branch);
}
