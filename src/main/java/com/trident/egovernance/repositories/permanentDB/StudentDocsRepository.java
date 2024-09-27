package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.StudentDocs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDocsRepository extends JpaRepository<StudentDocs,Integer> {
}
