package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "STAFFSTATUS")
@Table(name = "STAFFSTATUS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StaffStatusEntity {

    @Id
    @Column(name = "CODE")
    private String code;

    @Column(name = "STATUS")
    private String status;
}
