package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Hostel;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    void updateHostelByRegdNoIn(BooleanString hostelOption,Set<String> regdNos);

    @Modifying
    @Query("UPDATE HOSTEL h SET h.hostelOption = :hostelOption, h.regdyear = h.regdyear+1, h.hostelChoice = :hostelChoice WHERE h.regdNo IN :regdNos")
    void updateHostelByRegdNoInNotOpted(BooleanString hostelOption,Set<String> regdNos, HostelChoice hostelChoice);

    @Modifying
    @Query(value = """
    WITH hostel_opted AS (
        SELECT regd_no, mr_no FROM fee_collection 
        WHERE particulars = 'HOSTEL ADVANCE' AND session_id = :prevSessionId AND regd_no IN :regdNos
    ),
    updated_hostel AS (
        UPDATE hostel
        SET choice = CASE 
                        WHEN regd_no IN (SELECT regd_no FROM hostel_opted) THEN :hostelChoiceYes
                        ELSE :hostelChoiceNo
                    END,
            opted = CASE 
                        WHEN regd_no IN (SELECT regd_no FROM hostel_opted) THEN :yes
                        ELSE :no
                    END
        WHERE regd_no IN :regdNos
        RETURNING regd_no
    ),
    updated_fee_collection AS (
        UPDATE fee_collection
        SET session_id = :newSessionId
        WHERE mr_no IN (SELECT mr_no FROM hostel_opted)
    )
    UPDATE mr_details
    SET particulars = :hostelFeesDescription
    WHERE mr_no IN (SELECT mr_no FROM hostel_opted);
    """, nativeQuery = true)
    void performBatchOperations(@Param("prevSessionId") String prevSessionId,
                                @Param("regdNos") Set<String> regdNos,
                                @Param("hostelChoiceYes") HostelChoice hostelChoiceYes,
                                @Param("hostelChoiceNo") HostelChoice hostelChoiceNo,
                                @Param("yes") BooleanString yes,
                                @Param("no") BooleanString no,
                                @Param("newSessionId") String newSessionId,
                                @Param("hostelFeesDescription") String hostelFeesDescription);
}
