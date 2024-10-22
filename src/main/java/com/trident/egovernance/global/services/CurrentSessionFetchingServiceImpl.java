package com.trident.egovernance.global.services;

import com.trident.egovernance.exceptions.InvalidStudentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurrentSessionFetchingServiceImpl implements CurrentSessionFetcherServices {
    private final EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(CurrentSessionFetchingServiceImpl.class);

    public CurrentSessionFetchingServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String fetchCurrentSessionForStudent(String regdNo){
        String sql = "SELECT SESSIONID " +
                "FROM FEECDEMO.CURRENT_SESSION WHERE REGDNO = :regdNo";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("regdNo",regdNo);
        Object result = query.getSingleResult();
        String session;
        if(result == null){
            logger.info("No current session found for student");
            throw new InvalidStudentException("No current session found for student");
        }
        else{
            session = result.toString();
        }
        return session;
    }
}
