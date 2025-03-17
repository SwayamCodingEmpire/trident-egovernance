package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.Staff;

import java.util.List;

public interface StaffService {
    void addStaff(StaffDetailsDto staffDetailsDto);
    List<List<Staff>> getALlStaff();
    Staff getStaffByUsername(String username);
    Boolean updateStaffDetails(StaffDetailsDto updatedStudent, String username);
    List<String> getAllStaffDetailsForInput(String entityName);
}
