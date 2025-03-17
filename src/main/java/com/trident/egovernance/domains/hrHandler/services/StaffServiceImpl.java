package com.trident.egovernance.domains.hrHandler.services;

import com.trident.egovernance.dto.StaffDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.Staff;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {
    @Override
    public void addStaff(StaffDetailsDto staffDetailsDto) {

    }

    @Override
    public List<List<Staff>> getALlStaff() {
        return List.of();
    }

    @Override
    public Staff getStaffByUsername(String username) {
        return null;
    }

    @Override
    public Boolean updateStaffDetails(StaffDetailsDto updatedStudent, String username) {
        return null;
    }

    @Override
    public List<String> getAllStaffDetailsForInput(String entityName) {
        return List.of();
    }
}
