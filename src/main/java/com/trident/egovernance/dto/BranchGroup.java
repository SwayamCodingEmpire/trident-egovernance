package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Branch;

import java.util.List;

public record BranchGroup(
        String course,
        List<BranchRecord> branches
) {
}
