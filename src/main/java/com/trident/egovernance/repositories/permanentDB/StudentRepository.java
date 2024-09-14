package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}
