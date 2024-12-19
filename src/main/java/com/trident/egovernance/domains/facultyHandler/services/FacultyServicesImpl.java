package com.trident.egovernance.domains.facultyHandler.services;

import com.trident.egovernance.global.services.MiscellaniousServices;
import org.springframework.stereotype.Service;

@Service
public class FacultyServicesImpl {
    private final MiscellaniousServices miscellaniousServices;

    public FacultyServicesImpl(MiscellaniousServices miscellaniousServices) {
        this.miscellaniousServices = miscellaniousServices;
    }
}
