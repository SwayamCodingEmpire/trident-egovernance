package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.StudentCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCareerRepository extends JpaRepository<StudentCareer, String> {
}