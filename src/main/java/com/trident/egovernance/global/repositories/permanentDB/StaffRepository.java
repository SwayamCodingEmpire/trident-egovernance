package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Staff;
import com.trident.egovernance.global.helpers.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.SQLException;
import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query("SELECT CASE WHEN COUNT(s) = 0 THEN 100 ELSE MAX(s.staffId) + 1 END FROM STAFFDETAILS s")
    Long getStaffId();

    @Modifying
    @Query("UPDATE STAFFDETAILS s SET s.staffName = :staffName, s.staffDept = :staffDept, s.staffDesignation = :staffDesignation, " +
            "s.staffCategory = :staffCategory, s.status = :status, s.role = :role, s.phoneNumber = :phoneNumber, s.address = :address, " +
            "s.email = :email, s.securityQuestion = :securityQuestion, s.securityAnswer = :securityAnswer, s.collegeName = :collegeName " +
            "WHERE s.username = :username")
    int updateStudent(
        String staffName,
        String staffDept,
        String staffDesignation,
        String staffCategory,
        String status,
        String role,
        String phoneNumber,
        String address,
        String email,
        String securityQuestion,
        String securityAnswer,
        String collegeName,
        String username
    ) throws DataIntegrityViolationException, ConstraintViolationException, SQLException;

    @Query("SELECT s FROM STAFFDETAILS s")
    List<Staff> getAllStaffs();

    @Query("SELECT s FROM STAFFDETAILS s WHERE s.username = :username")
    List<Staff> getStaffByUsername(String username);
}
