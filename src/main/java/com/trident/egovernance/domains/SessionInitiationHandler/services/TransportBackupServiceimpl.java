package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.OldTransportRepository;
import com.trident.egovernance.global.repositories.permanentDB.TransportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

    @Async
    public Boolean transferToOldTransport(Set<String> regdNos) {
        try{
            oldTransportRepository.saveTransportToOld(regdNos);
//            transportRepository.deleteAllByRegdNoIn(regdNos);
            transportRepository.updateRegdYear(regdNos);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean deleteFromTransport(Set<String> regdNos){
        transportRepository.deleteAllByRegdNoIn(regdNos);
        return true;
    }
}
