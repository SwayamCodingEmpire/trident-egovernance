package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Student;
import com.trident.egovernance.global.entities.redisEntities.NSR;
import com.trident.egovernance.global.helpers.*;

public record DuesDetailsInitiationDTO(
        String regdNo,
        StudentType studentType,
        BooleanString indortrng,
        BooleanString plpoolm,
        TFWType tfw,
        BooleanString transportOpted,
        BooleanString hostelOption,
        HostelChoice hostelChoice,
        Integer currentYear,
        Courses course,
        String batchId
) {
    public DuesDetailsInitiationDTO(NSR nsr) {
        this(
                nsr.getRegdNo(),
                nsr.getStudentType(),
                nsr.getIndortrng(),
                nsr.getPlpoolm(),
                nsr.getTfw(),
                nsr.getTransportOpted(),
                nsr.getHostelOption(),
                nsr.getHostelChoice(),
                nsr.getCurrentYear(),
                nsr.getCourse(),
                nsr.getBatchId()
        );
    }

    public DuesDetailsInitiationDTO(Student nsr) {
        this(
                nsr.getRegdNo(),
                nsr.getStudentType(),
                nsr.getIndortrng(),
                nsr.getPlpoolm(),
                nsr.getStudentAdmissionDetails().getTfw(),
                nsr.getTransport().getTransportOpted(),
                nsr.getHostel().getHostelOption(),
                nsr.getHostel().getHostelChoice(),
                nsr.getCurrentYear(),
                nsr.getCourse(),
                nsr.getBatchId()
        );
    }
}
