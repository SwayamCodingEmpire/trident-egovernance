package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Sessions;
import com.trident.egovernance.helpers.SessionIdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionsRepository extends JpaRepository<Sessions, SessionIdId> {
}
