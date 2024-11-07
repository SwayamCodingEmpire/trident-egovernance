package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Hostel;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface HostelRepository extends JpaRepository<Hostel,String> {
    List<Hostel> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    long deleteByRegdNoIn(Set<String> regdNos);

    @Modifying
    @Transactional
    @Query("UPDATE HOSTEL s SET "
            + "s.hostelier = :hostelier, "
            + "s.hostelOption = :hostelOption, "
            + "s.hostelChoice = :hostelChoice, "
            + "s.lgName = :lgName, "
            + "s.regdyear = :regdyear "
            + "WHERE s.student.regdNo = :regdNo")
    int updateStudentHostelDetails(
            BooleanString hostelier,
            BooleanString hostelOption,
            HostelChoice hostelChoice,
            String lgName,
            Integer regdyear,
            String regdNo
    );



    @Modifying
    @Query("UPDATE HOSTEL h SET h.hostelOption = :hostelOption, h.regdyear = h.regdyear+1 WHERE h.regdNo IN :regdNos")
    long updateHostelByRegdNoIn(BooleanString hostelOption,Set<String> regdNos);

    @Modifying
    @Query("UPDATE HOSTEL h SET h.hostelOption = :hostelOption, h.regdyear = h.regdyear+1, h.hostelChoice = :hostelChoice WHERE h.regdNo IN :regdNos")
    long updateHostelByRegdNoInNotOpted(BooleanString hostelOption,Set<String> regdNos, HostelChoice hostelChoice);
}
