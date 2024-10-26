package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Sessions;
import com.trident.egovernance.global.helpers.SessionIdId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Sessions, SessionIdId> {
}
