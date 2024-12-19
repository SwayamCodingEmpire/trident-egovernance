package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.SubjectResultData;
import com.trident.egovernance.global.entities.views.SemesterResult;
import com.trident.egovernance.global.helpers.SemesterResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface SemesterResultRepository extends JpaRepository<SemesterResult, SemesterResultId> {

    @Query("SELECT s FROM SemesterResult s LEFT JOIN FETCH s.subjectDetails WHERE s.regdNo = :regdNo")
    List<SemesterResult> findAllByRegdNo(String regdNo);  // Eager loading

}
