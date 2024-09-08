package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;

import java.util.List;

public interface NSRService {
    NSRDto postNSRData(NSRDto nsrDto);
    NSRDto getNSRDataByRollNo(String rollNo);
    List<NSRDto> getAllNSRData();
}
