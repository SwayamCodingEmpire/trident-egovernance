package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.dto.TransportOnlyDTO;
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
        @ToString.Exclude
        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REGDNO")
        private Student student; // VARCHAR2(100)

        // Constructor to initialize Transport from DTO
        public Transport(TransportOnlyDTO transportOnlyDTO) {
                super(transportOnlyDTO.regdNo(),
                        transportOnlyDTO.transportAvailed(),
                        transportOnlyDTO.transportOpted(),
                        transportOnlyDTO.route(),
                        transportOnlyDTO.pickUpPoint(),
                        transportOnlyDTO.regdYear());
        }
}
