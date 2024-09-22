package com.trident.egovernance.entities.permanentDB;

import com.trident.egovernance.helpers.BooleanString;
import com.trident.egovernance.helpers.HostelChoice;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "TRANSPORT")
@Table(name = "TRANSPORT")
public class Transport {
        @Id
        @Column(name = "REGDNO")
        private String regdNo;
        @Column(name = "TRANSPORTAVAILED")
        @Enumerated(EnumType.STRING)
        private BooleanString transportAvailed=BooleanString.NO;
        @Enumerated(EnumType.STRING)
        @Column(name = "TRANSPORTOPTED")
        private BooleanString transportOpted;
        @Column(name = "ROUTE")
        private String route;
        @Column(name = "PICKUPPOINT")
        private String pickUpPoint;
        @Column(name = "REGDYEAR")
        private Integer regdyear;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REGDNO")
        private Student student; // VARCHAR2(100)
}
