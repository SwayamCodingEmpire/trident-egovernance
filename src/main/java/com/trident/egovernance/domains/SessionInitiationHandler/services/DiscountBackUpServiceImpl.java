package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.global.repositories.permanentDB.DiscountRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldAdjustmentRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldDiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class DiscountBackUpServiceImpl {
    private final DiscountRepository discountRepository;
    private final OldDiscountRepository oldDiscountRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(DiscountBackUpServiceImpl.class);

    public DiscountBackUpServiceImpl(DiscountRepository discountRepository, OldDiscountRepository oldDiscountRepository, JdbcTemplate jdbcTemplate) {
        this.discountRepository = discountRepository;
        this.oldDiscountRepository = oldDiscountRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Boolean saveToOldDiscount(List<String> regdNos) {
        oldDiscountRepository.saveToOldDiscount(regdNos);
        return true;
    }

    public Boolean deleteFromDiscounts(List<String> regdNo) {
        discountRepository.deleteByRegdNoIn(regdNo);
        return true;
    }
}
