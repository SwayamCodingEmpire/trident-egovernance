package com.trident.egovernance.global.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class BranchId {
    private String branchCode;
    private String course;  // Changed from Courses to String

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BranchId branchId = (BranchId) o;
        return Objects.equals(branchCode, branchId.getBranchCode()) &&
                Objects.equals(course, branchId.getCourse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(branchCode, course);
    }
}