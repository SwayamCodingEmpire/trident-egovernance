package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, Long> {

    @Query(value = "SELECT * FROM SECTIONS WHERE SECTIONID = :sectionId", nativeQuery = true)
    Map<String, Object> findSectionRawById(Long sectionId);
}
