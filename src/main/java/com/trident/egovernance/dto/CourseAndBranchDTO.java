package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;

import java.util.List;

public record CourseAndBranchDTO(
        Courses course,
        List<String> branches
) {}
