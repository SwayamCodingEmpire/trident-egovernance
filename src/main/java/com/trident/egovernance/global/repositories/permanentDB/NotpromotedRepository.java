package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.RegdOnly;
import com.trident.egovernance.global.entities.permanentDB.Notpromoted;
import com.trident.egovernance.global.helpers.NotPromotedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NotpromotedRepository extends JpaRepository<Notpromoted, NotPromotedId> {
    Set<RegdOnly> findAllByCurrentYearAndSessionId(Integer currentYear, String sessionId);
}