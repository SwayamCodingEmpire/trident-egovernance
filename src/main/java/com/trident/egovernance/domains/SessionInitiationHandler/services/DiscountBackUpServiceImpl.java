package com.trident.egovernance.domains.SessionInitiationHandler.services;

import com.trident.egovernance.global.repositories.permanentDB.DiscountRepository;
import com.trident.egovernance.global.repositories.permanentDB.OldDiscountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<Boolean> transferToOldDiscount(Set<String> regdNos, TransactionStatus status) {
        try{
            oldDiscountRepository.saveToOldDiscount(regdNos);
            discountRepository.deleteByRegdNoIn(regdNos);
            return CompletableFuture.completedFuture(true);
        }catch (Exception e){
            status.setRollbackOnly();
            return CompletableFuture.completedFuture(false);
        }
    }

    public Boolean deleteFromDiscounts(Set<String> regdNo) {
        discountRepository.deleteByRegdNoIn(regdNo);
        return true;
    }
}