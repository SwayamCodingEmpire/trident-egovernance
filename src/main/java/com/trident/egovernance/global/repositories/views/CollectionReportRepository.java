package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.dto.CollectionReportDTO;
import com.trident.egovernance.global.entities.views.CollectionReport;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface CollectionReportRepository extends JpaRepository<CollectionReport, Long> {
//    @Query("SELECT new com.trident.egovernance.dto.CollectionReportDTO(c, new )" +
//            "FROM CollectionReport c " +
//            "JOIN MRDETAILS md ON c.mrNo = md.feeCollection.mrNo")
//    List<CollectionReportDTO> findAllCollectionReportsWithMrDetails();

    @Query("SELECT cr, md.slNo, md.particulars, md.amount " +
            "FROM CollectionReport cr " +
            "JOIN MRDETAILS md ON cr.mrNo = md.feeCollection.mrNo WHERE cr.paymentDate = :paymentDate")
    List<Tuple> findAllCollectionReportsWithMrDetailsByDate(String paymentDate);

    @Query("SELECT cr, md.slNo, md.particulars, md.amount " +
            "FROM CollectionReport cr " +
            "JOIN MRDETAILS md ON cr.mrNo = md.feeCollection.mrNo WHERE TO_DATE(cr.paymentDate,'DD-MM-YYYY') BETWEEN :startDate AND :endDate")
    List<Tuple> findAllCollectionReportsWithMrDetailsByDateInBetween(Date startDate, Date endDate);

    @Query("SELECT cr, md.slNo, md.particulars, md.amount " +
            "FROM CollectionReport cr " +
            "JOIN MRDETAILS md ON cr.mrNo = md.feeCollection.mrNo WHERE cr.paymentDate IN :paymentDate")
    List<Tuple> findAllCollectionReportsWithMrDetailsByDateIn(Set<String> paymentDate);
}
