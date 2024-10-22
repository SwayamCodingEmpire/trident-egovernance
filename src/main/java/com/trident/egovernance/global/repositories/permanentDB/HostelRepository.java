package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HostelRepository extends JpaRepository<Hostel,String> {
    List<Hostel> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    long deleteByRegdNoIn(List<String> regdNos);
}
