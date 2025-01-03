package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.FeeCollectionView;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface FeeCollectionViewRepository extends JpaRepository<FeeCollectionView, Long> {

    @Query("SELECT f FROM FEECOLLECTIONVIEW f LEFT JOIN FETCH f.mrDetails WHERE f.paymentDate = :paymentDate")
    List<FeeCollectionView> findAllCollectionReportsWithMrDetailsByDate(Date paymentDate);

    @Query("SELECT f FROM FEECOLLECTIONVIEW f LEFT JOIN FETCH f.mrDetails WHERE f.paymentDate BETWEEN :startDate AND :endDate")
    List<FeeCollectionView> findAllCollectionReportsWithMrDetailsBetweenDate(Date startDate, Date endDate);
    @Query("SELECT f FROM FEECOLLECTIONVIEW f LEFT JOIN FETCH f.mrDetails WHERE f.sessionId = :sessionId")
    List<FeeCollectionView> findFeeCollectionViewBySessionId(String sessionId);


}
