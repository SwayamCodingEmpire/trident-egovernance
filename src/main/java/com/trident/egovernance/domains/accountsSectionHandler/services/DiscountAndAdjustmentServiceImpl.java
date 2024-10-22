package com.trident.egovernance.domains.accountsSectionHandler.services;

import com.trident.egovernance.dto.CurrentSessionDto;
import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import com.trident.egovernance.global.entities.permanentDB.Discount;
import com.trident.egovernance.exceptions.InvalidStudentException;
import com.trident.egovernance.domains.accountsSectionHandler.DiscountAndAdjustmentService;
import com.trident.egovernance.global.repositories.permanentDB.AdjustmentsRepository;
import com.trident.egovernance.global.repositories.permanentDB.DiscountRepository;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
class DiscountAndAdjustmentServiceImpl implements DiscountAndAdjustmentService {
    private final EntityManager entityManager;
    private final StudentRepository studentRepository;
    private final DiscountRepository discountRepository;
    private final AdjustmentsRepository adjustmentsRepository;
    private final Logger logger = LoggerFactory.getLogger(DiscountAndAdjustmentServiceImpl.class);

    public DiscountAndAdjustmentServiceImpl(EntityManager entityManager, StudentRepository studentRepository, DiscountRepository discountRepository, AdjustmentsRepository adjustmentsRepository) {
        this.entityManager = entityManager;
        this.studentRepository = studentRepository;
        this.discountRepository = discountRepository;
        this.adjustmentsRepository = adjustmentsRepository;
    }

    @Transactional
    public Boolean insertDiscountData(Discount discount) {
        if(discount.getRegdNo().compareTo("ALL")==0){
            String sql = "SELECT REGDNO " +
                    "FROM FEECDEMO.CURRENT_SESSION WHERE CURRENTYEAR = :currentYear";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("currentYear", discount.getCurrentYear());
            List<String> regdNos = query.getResultList();
            if (regdNos.isEmpty()) {
                logger.info("No students found for the current year");
                return false;
            } else {
                List<Discount> discounts = new ArrayList<>();
                for (String regdNo : regdNos) {
                    Discount discountTemp = new Discount();
                    discountTemp.setCurrentYear(discount.getCurrentYear());
                    discountTemp.setStaffId(discount.getStaffId());
                    discountTemp.setDiscount(discount.getDiscount());
                    discountTemp.setParticulars(discount.getParticulars());
                    discountTemp.setRegdNo(regdNo);
                    discounts.add(discountTemp);
                }
                discountRepository.saveAllAndFlush(discounts);
                return true;
            }
        }
        else {
            if(!studentRepository.existsById(discount.getRegdNo())){
                throw new InvalidStudentException("Invalid Registration Number");
            }
            String sql = "SELECT CS " +
                    "FROM FEECDEMO.CURRENT_SESSION CS WHERE CS.CURRENTYEAR = :currentYear AND CS.REGDNO = :regdNo";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter("currentYear", discount.getCurrentYear());
            query.setParameter("regdNo", discount.getRegdNo());
            CurrentSessionDto currentSessionDto = (CurrentSessionDto) query.getResultList();
//            CurrentSessionDto currentSessions = rows.stream()
//                            .map(row->new CurrentSessionDto(
//                                    (String) row[0],  // REGDNO
//                                    (String) row[1],  // COURSE
//                                    (Integer) row[2], // CURRENTYEAR
//                                    (String) row[3],  // SESSIONID
//                                    (String) row[4],  // PREVSESSIONSID
//                                    (Date) row[5]     // STARTDATE
//
//                            )).toList();
            discount.setRegdYear(discount.getCurrentYear());
            discount.setSessionId(currentSessionDto.getSessionId());
            logger.info(currentSessionDto.toString());
            discountRepository.save(discount);
            return true;
        }
    }

    @Transactional
    public Adjustments addAdjustment(Adjustments adjustments){
        if(!studentRepository.existsById(adjustments.getRegdNo())){
            throw new InvalidStudentException("Invalid Registration Number");
        }
        return adjustmentsRepository.save(adjustments);
    }
}
