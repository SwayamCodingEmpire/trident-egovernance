package com.trident.egovernance.global.entities.permanentDB;

import com.trident.egovernance.global.helpers.BooleanString;
import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public abstract sealed class BaseTransport permits Transport,OldTransport {
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
    private Integer regdYear;

    public BaseTransport(BaseTransport baseTransport) {
        this.regdNo = baseTransport.regdNo;
        this.transportAvailed = baseTransport.transportAvailed;
        this.transportOpted = baseTransport.transportOpted;
        this.route = baseTransport.route;
        this.pickUpPoint = baseTransport.pickUpPoint;
        this.regdYear = baseTransport.regdYear;
    }
}
