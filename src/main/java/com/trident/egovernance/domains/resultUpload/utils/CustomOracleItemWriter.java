package com.trident.egovernance.domains.resultUpload.utils;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import com.trident.egovernance.global.repositories.examDB.ResultCustomDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomOracleItemWriter implements ItemWriter<StudentExamResults>{

    private final Logger logger = LoggerFactory.getLogger(CustomOracleItemWriter.class);

    private final ResultCustomDatabase resultCustomDatabase;

    public CustomOracleItemWriter(ResultCustomDatabase resultCustomDatabase) {
        this.resultCustomDatabase = resultCustomDatabase;
    }

    @Override
    public void write(Chunk<? extends StudentExamResults> chunk){
        List<? extends StudentExamResults> items = chunk.getItems();

        if(items != null && !items.isEmpty()){

            List<StudentExamResults> studentExamResults = new ArrayList<>(items.size());
            for(StudentExamResults item : items){
                studentExamResults.add(item);
            }
            try{
                logger.info("Attempting to write batch of {} StudentExamResults to Oracle DB.", items.size());

                resultCustomDatabase.invokeInsCustomDatabaseBatch(studentExamResults);
                logger.info("Successfully wrote batch of {} StudentExamResults to Oracle DB.", studentExamResults.size());
            } catch(SQLException e){
                logger.error("SQL Error during batch write of {} items: {}", studentExamResults.size(), e.getMessage(), e);
                throw new RuntimeException("Database batch write failed: " + e.getMessage(), e);
            } catch(Exception e){
                logger.error("Unexpected error during batch write of {} items: {}", studentExamResults.size(), e.getMessage(), e);
                throw new RuntimeException("Unexpected error during batch write: " + e.getMessage(), e);
            }
        }
    }
}
