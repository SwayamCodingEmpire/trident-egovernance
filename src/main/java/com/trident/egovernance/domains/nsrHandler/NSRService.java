package com.trident.egovernance.domains.nsrHandler;

import com.trident.egovernance.dto.NSRDto;
import com.trident.egovernance.global.entities.redisEntities.NSR;

import java.util.List;
import java.util.Set;

public interface NSRService {
    void bulkSaveNSRData(List<NSR> nsrs);
    NSRDto postNSRData(NSR nsr);
    NSRDto postNSRDataByStudent(NSR nsr);
    NSRDto getNSRDataByRollNo(String rollNo);
    List<NSRDto> getAllNSRData();
    NSRDto getNSRDataByJeeApplicationNo(String jeeApplicationNo);
    Boolean saveToPermanentDatabase(String jeeApplicationNo);
    Set<NSRDto> getAllNSRDataByAdmissionYear(String admissionYear);
}
