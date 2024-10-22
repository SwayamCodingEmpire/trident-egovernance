package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@Entity(name = "OLDTRANSPORT")
@Table(name = "OLDTRANSPORT")
@ToString
public final class OldTransport extends BaseTransport {
    public OldTransport(BaseTransport baseTransport) {
        super(baseTransport);
    }
}
