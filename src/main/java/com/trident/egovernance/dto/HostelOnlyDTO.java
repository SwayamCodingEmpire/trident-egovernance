package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Hostel;
import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.HostelChoice;

public record HostelOnlyDTO(
        String regdNo,
        BooleanString hostelier,
        BooleanString hostelOption,
        HostelChoice hostelChoice,
        String lgName,
        Integer regdyear
) {
    public HostelOnlyDTO(Hostel hostel){
        this(
                hostel.getRegdNo(),
                hostel.getHostelier(),
                hostel.getHostelOption(),
                hostel.getHostelChoice(),
                hostel.getLgName(),
                hostel.getRegdyear()
        );
    }
}
