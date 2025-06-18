package com.trident.egovernance.domains.resultUpload.services;

import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface ResultProcessingService {

    void processResultCSV(MultipartFile file, String exmaType, int semester, String branch) throws SQLException;
}
