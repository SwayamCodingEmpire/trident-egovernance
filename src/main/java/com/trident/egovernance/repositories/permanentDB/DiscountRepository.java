package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
}
