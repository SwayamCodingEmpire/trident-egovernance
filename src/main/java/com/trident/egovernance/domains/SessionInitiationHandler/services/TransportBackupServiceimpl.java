package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.entities.permanentDB.Transport;
import com.trident.egovernance.global.repositories.permanentDB.OldTransportRepository;
import com.trident.egovernance.global.repositories.permanentDB.TransportRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@Service
public class TransportBackupServiceimpl {
    private final TransportRepository transportRepository;
    private final OldTransportRepository oldTransportRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(TransportBackupServiceimpl.class);

    public TransportBackupServiceimpl(TransportRepository transportRepository, OldTransportRepository oldTransportRepository, JdbcTemplate jdbcTemplate) {
        this.transportRepository = transportRepository;
        this.oldTransportRepository = oldTransportRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean saveToOldTransport(List<String> regdNos){
        oldTransportRepository.saveTransportToOld(regdNos);
        return true;
    }

    public Boolean deleteFromTransport(List<String> regdNos){
        transportRepository.deleteAllByRegdNoIn(regdNos);
        return true;
    }
}
