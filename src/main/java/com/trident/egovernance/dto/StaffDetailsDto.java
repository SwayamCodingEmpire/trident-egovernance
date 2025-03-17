package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public record StaffDetailsDto(
        Long staffId,
        String staffName,
        String staffDept,
        String staffDesignation,
        String staffCategory,
        String status,
        String role,
        String username,
        String phoneNumber,
        String password,
        String address,
        String email,
        String securityQuestion,
        String securityAnswer,
        String collegeName
) {
    public StaffDetailsDto(Staff staffDetailsEntity) {
        this(
                staffDetailsEntity.getStaffId(),
                staffDetailsEntity.getStaffName(),
                staffDetailsEntity.getStaffDept(),
                staffDetailsEntity.getStaffDesignation(),
                staffDetailsEntity.getStaffCategory(),
                staffDetailsEntity.getStatus(),
                staffDetailsEntity.getRole(),
                staffDetailsEntity.getUsername(),
                staffDetailsEntity.getPhoneNumber(),
                staffDetailsEntity.getPassword(),
                staffDetailsEntity.getAddress(),
                staffDetailsEntity.getEmail(),
                staffDetailsEntity.getSecurityQuestion(),
                staffDetailsEntity.getSecurityAnswer(),
                staffDetailsEntity.getCollegeName()
        );
    }
}

