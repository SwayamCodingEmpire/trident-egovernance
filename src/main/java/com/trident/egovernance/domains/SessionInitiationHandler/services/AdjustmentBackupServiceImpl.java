package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.AdjustmentsRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldAdjustmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdjustmentBackupServiceImpl {
    private final AdjustmentsRepository adjustmentsRepository;
    private final OldAdjustmentRepository oldAdjustmentRepository;
    private final Logger logger = LoggerFactory.getLogger(AdjustmentBackupServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public AdjustmentBackupServiceImpl(AdjustmentsRepository adjustmentsRepository, OldAdjustmentRepository oldAdjustmentRepository, JdbcTemplate jdbcTemplate) {
        this.adjustmentsRepository = adjustmentsRepository;
        this.oldAdjustmentRepository = oldAdjustmentRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean transferToOldAdjustment(List<String> regdNos) {
        oldAdjustmentRepository.saveAdjustmentsToOld(regdNos);
        adjustmentsRepository.deleteAllByRegdNoIn(regdNos);
        return true;
    }

    public Boolean deleteFromAdjustments(List<String> regdNos) {
        adjustmentsRepository.deleteAllByRegdNoIn(regdNos);
        return true;
    }
}
