package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;

public interface MapperService {
    NSRDto convertToNSRDto(NSR nsr);
    NSR convertToNSR(NSRDto nsrDto);
}
