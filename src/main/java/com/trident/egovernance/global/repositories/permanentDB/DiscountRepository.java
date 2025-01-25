package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
    List<Discount> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    @Query("DELETE FROM DISCOUNT d WHERE d.regdNo IN :regdNos")
    long deleteByRegdNoIn(Set<String> regdNos);
}
