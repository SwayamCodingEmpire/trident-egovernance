package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.OldTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldTransportRepository extends JpaRepository<OldTransport,String> {
    @Modifying
    @Query(value = """
INSERT INTO OLDTRANSPORT (REGDNO, TRANSPORTAVAILED, TRANSPORTOPTED, ROUTE, PICKUPPOINT, REGDYEAR)
SELECT 
        REGDNO, 
        TRANSPORTAVAILED, 
        TRANSPORTOPTED, 
        ROUTE, 
        PICKUPPOINT, 
        REGDYEAR
    FROM 
        TRANSPORT 
    WHERE 
        REGDNO IN (:regdNos)
""", nativeQuery = true)
    void saveTransportToOld(@Param("regdNos") List<String> regdNos);
}
