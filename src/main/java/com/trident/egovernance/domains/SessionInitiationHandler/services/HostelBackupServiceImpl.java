package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.HostelRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldHostelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class HostelBackupServiceImpl {
    private final HostelRepository hostelRepository;
    private final OldHostelRepository oldHostelRepository;
    private final Logger logger = LoggerFactory.getLogger(HostelBackupServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public HostelBackupServiceImpl(HostelRepository hostelRepository, OldHostelRepository oldHostelRepository, JdbcTemplate jdbcTemplate) {
        this.hostelRepository = hostelRepository;
        this.oldHostelRepository = oldHostelRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean transferToOldHostel(Set<String> regdNos) {
        try {
            oldHostelRepository.saveHostelToOld(regdNos);
//            hostelRepository.deleteByRegdNoIn(regdNos);
//            hostelRepository.updateHostelByRegdNoIn()
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean deleteFromHostel(Set<String> regdNos) {
        hostelRepository.deleteByRegdNoIn(regdNos);
        return true;
    }
    }
