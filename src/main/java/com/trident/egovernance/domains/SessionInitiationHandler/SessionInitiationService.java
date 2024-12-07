package com.trident.egovernance.domains.SessionInitiationHandler;

import com.trident.egovernance.dto.SessionInitiationDTO;
import com.trident.egovernance.dto.SessionInitiationData;
import com.trident.egovernance.global.entities.permanentDB.Sessions;

public interface SessionInitiationService {
    Sessions createNewSession(SessionInitiationData sessionInitiationDTO);
}
