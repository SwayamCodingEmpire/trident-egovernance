package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.Student_Test;
import com.trident.egovernance.global.helpers.Courses;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface StudentTestRepository extends JpaRepository<Student_Test, String> {
    List<Student_Test> findAllByCourse(Courses course);
}
