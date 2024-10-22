package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport,String> {
    List<Transport> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    long deleteAllByRegdNoIn(List<String> regdNos);
}
