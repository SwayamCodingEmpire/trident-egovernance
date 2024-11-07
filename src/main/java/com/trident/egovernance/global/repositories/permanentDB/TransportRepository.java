package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Transport;
import com.trident.egovernance.global.helpers.BooleanString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface TransportRepository extends JpaRepository<Transport,String> {
    List<Transport> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    long deleteAllByRegdNoIn(Set<String> regdNos);


    @Modifying
    @Transactional
    @Query("UPDATE TRANSPORT t SET "
            + "t.transportAvailed = :transportAvailed, "
            + "t.transportOpted = :transportOpted, "
            + "t.route = :route, "
            + "t.pickUpPoint = :pickUpPoint, "
            + "t.regdYear = :regdYear "
            + "WHERE t.regdNo = :regdNo")
    int updateTransportDetails(
            BooleanString transportAvailed,
            BooleanString transportOpted,
            String route,
            String pickUpPoint,
            Integer regdYear,
            String regdNo
    );

    @Modifying
    @Query("UPDATE TRANSPORT t SET t.regdYear = t.regdYear + 1 WHERE t.regdNo IN :regdNo")
    int updateRegdYear(Set<String> regdNo);
}
