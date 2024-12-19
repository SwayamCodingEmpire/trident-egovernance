package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.AttendanceSummaryDTO;
import com.trident.egovernance.global.entities.views.StudAttView;
import com.trident.egovernance.global.helpers.StudAttViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudAttViewRepository extends JpaRepository<StudAttView, StudAttViewId> {
    @Query("SELECT new com.trident.egovernance.dto.AttendanceSummaryDTO(s.sem, s.subject, s.classesHeld, s.classesAttended) FROM STUDATTVIEW3 s WHERE s.regdNo = :regdNo")
    List<AttendanceSummaryDTO> findAllByRegdNo(String regdNo);
}
