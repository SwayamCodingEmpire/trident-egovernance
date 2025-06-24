package com.trident.egovernance.global.repositories.examDB;

import com.trident.egovernance.global.entities.examDB.StudentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentMasterRepo extends JpaRepository<StudentMaster,String> {

    @Query("SELECT s.subcode, s.credit FROM StudentMaster s")
    List<Object[]> findAllStudentCodesAndCredits();

    List<StudentMaster> findAllStudent();
}
