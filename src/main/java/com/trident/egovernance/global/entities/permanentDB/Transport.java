package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "TRANSPORT")
@Table(name = "TRANSPORT")
public final class Transport extends BaseTransport {
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REGDNO")
        private Student student; // VARCHAR2(100)
}
