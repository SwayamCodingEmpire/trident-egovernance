package com.trident.egovernance.services;

import com.trident.egovernance.dtos.NSRDto;
import com.trident.egovernance.entities.redisEntities.NSR;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MapperServiceImpl implements MapperService {
    private final ModelMapper modelMapper;

    public MapperServiceImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public NSRDto convertToNSRDto(NSR nsr) {
        return modelMapper.map(nsr,NSRDto.class);
    }

    @Override
    public NSR convertToNSR(NSRDto nsrDto) {
        return modelMapper.map(nsrDto,NSR.class);
    }
}
