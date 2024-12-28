package com.trident.egovernance.dto;

public record BranchRecord(
        String branchCode,
        String branch,
        String course,
        Integer courseInProgress
) {
}
