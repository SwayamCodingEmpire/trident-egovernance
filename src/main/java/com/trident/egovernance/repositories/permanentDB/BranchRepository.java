package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch,String> {
}