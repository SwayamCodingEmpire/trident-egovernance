package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.RegdOnly;
import com.trident.egovernance.global.entities.permanentDB.Notpromoted;
import com.trident.egovernance.global.helpers.NotPromotedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NotpromotedRepository extends JpaRepository<Notpromoted, NotPromotedId> {
    Set<RegdOnly> findAllByCurrentYearAndSessionId(Integer currentYear, String sessionId);

    List<Notpromoted> findByCurrentYear(Integer currentYear);
    List<Notpromoted> findBySessionId(String sessionId);
    @Query("SELECT e FROM NOTPROMOTED e WHERE (:regdNo IS NULL OR e.regdNo = :regdNo) " +
            "AND (:currentYear IS NULL OR e.currentYear = :currentYear) " +
            "AND (:sessionId IS NULL OR e.sessionId = :sessionId)")
    List<Notpromoted> findNotPromoted(String regdNo,
                                Integer currentYear,
                                String sessionId);

}