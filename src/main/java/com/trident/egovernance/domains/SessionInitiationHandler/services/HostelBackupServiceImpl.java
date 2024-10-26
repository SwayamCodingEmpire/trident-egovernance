package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.HostelRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldHostelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public Boolean transferToOldHostel(List<String> regdNos) {
        oldHostelRepository.saveHostelToOld(regdNos);
        hostelRepository.deleteByRegdNoIn(regdNos);
        return true;
    }

    public Boolean deleteFromHostel(List<String> regdNos) {
        hostelRepository.deleteByRegdNoIn(regdNos);
        return true;
    }
    }
