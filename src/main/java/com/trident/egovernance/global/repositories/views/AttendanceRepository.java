package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.AttendanceSummaryDTO;
import com.trident.egovernance.global.entities.views.Attendance;
import com.trident.egovernance.global.helpers.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

//    @Query("SELECT new com.trident.egovernance.dto.AttendanceSummaryDTO(a.subAbbr, a.totalClasses, a.totalAttended) FROM ATTENDANCE a WHERE a.regdNo = :regdNo")
//    List<AttendanceSummaryDTO> findAllByRegdNo(String regdNo);
}
