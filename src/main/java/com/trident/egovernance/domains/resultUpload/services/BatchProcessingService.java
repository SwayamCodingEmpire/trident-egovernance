package com.trident.egovernance.domains.resultUpload.services;

import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface BatchProcessingService {

    void processResultCSV(MultipartFile file, String exmaType, int semester, String branch, String resultPublishDate) throws SQLException;
}
