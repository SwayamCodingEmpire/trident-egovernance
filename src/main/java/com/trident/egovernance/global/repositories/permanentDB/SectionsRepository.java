package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, Long> {
}
