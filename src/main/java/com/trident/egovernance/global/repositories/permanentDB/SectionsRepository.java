package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, Long> {

    @Query(value = "SELECT * FROM SECTIONS WHERE SECTIONID = :sectionId", nativeQuery = true)
    Map<String, Object> findSectionRawById(Long sectionId);

    @Query("SELECT MAX(s.sectionId)+1 FROM SECTIONS s")
    Long getMaxId();

    @Query("SELECT s FROM SECTIONS s LEFT JOIN FETCH s.rollSheets WHERE s.course = :course AND s.sem = :sem AND s.branchCode = :branchCode AND s.section = :section")
    Optional<Sections> findAllByCourseAndSemAndBranchCodeAndSection(String course, Integer sem, String branchCode, String section);

    @Query("SELECT s.sectionId FROM SECTIONS s WHERE s.course = :course AND s.sem = :sem AND s.branchCode = :branchCode AND s.section = :section")
    Long findSectionIdByCourseAndSemAndBranchCodeAndSection(String course, Integer sem, String branchCode, String section);
}
