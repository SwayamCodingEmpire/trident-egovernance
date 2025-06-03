package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.Staff;

import java.util.List;
import java.util.Map;

public interface StaffService {
    Staff addStaff(StaffDetailsDto staffDetailsDto);

    List<List<Staff>> getALlStaff();

    List<Staff> getStaffByUsername(String username);

    Boolean updateStaffDetails(StaffDetailsDto updatedStudent, String username);

    Map<String, List<Object>> getAllStaffDetailsForInput(String entityName);

    Boolean finalSubmitStaff(Long staffId);
}
