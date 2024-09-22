package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;

import java.util.List;

public interface NSRService {
    NSRDto postNSRData(NSR nsr);
    NSRDto getNSRDataByRollNo(String rollNo);
    List<NSRDto> getAllNSRData();
    NSRDto getNSRDataByJeeApplicationNo(String jeeApplicationNo);
    Boolean saveToPermanentDatabase(String jeeApplicationNo);
}
