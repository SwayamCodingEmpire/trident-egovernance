package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.dto.CurrentSessionDto;
import com.trident.egovernance.entities.permanentDB.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    @Query(value = "SELECT REGDNO, COURSE, CURRENTYEAR, SESSIONID, PREVSESSIONSID, STARTDATE " +
            "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo", nativeQuery = true)
    List<CurrentSessionDto> getCurrentSessionDtoByRegdNo(@Param("regdNo") String regdNo);
}
