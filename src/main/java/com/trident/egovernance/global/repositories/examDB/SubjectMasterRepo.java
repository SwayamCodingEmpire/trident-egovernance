package com.trident.egovernance.global.repositories.examDB;

import com.trident.egovernance.global.entities.examDB.SubjectMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectMasterRepo extends JpaRepository<SubjectMaster,String> {

    @Query("SELECT s.subjectCode, s.credit FROM SubjectMaster s")
    List<Object[]> findAllStudentCodesAndCredits();

    @Query("SELECT s FROM SubjectMaster s")
    List<SubjectMaster> retrieveAllStudents();
}
