package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Transport;
import com.trident.egovernance.global.helpers.BooleanString;

public record TransportOnlyDTO(
        String regdNo,
        BooleanString transportAvailed,
        BooleanString transportOpted,
        String route,
        String pickUpPoint,
        Integer regdYear
) implements StudentUpdateDTO {
    public TransportOnlyDTO(Transport transport) {
        this(
                transport.getRegdNo(),
                transport.getTransportAvailed(),
                transport.getTransportOpted(),
                transport.getRoute(),
                transport.getPickUpPoint(),
                transport.getRegdYear()
        );
    }
}
