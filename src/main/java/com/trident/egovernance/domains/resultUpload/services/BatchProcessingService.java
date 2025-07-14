package com.trident.egovernance.domains.resultUpload.services;

import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface BatchProcessingService {

    void startResultProcessingJob(MultipartFile file, String examType, int semester, String branch, String academicYear, String userMail);
}
