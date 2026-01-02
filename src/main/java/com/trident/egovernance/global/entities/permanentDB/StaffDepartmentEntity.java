package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "STAFFDEPT")
@Table(name = "STAFFDEPT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StaffDepartmentEntity {

    @Column(name = "DEPT_ID")
    @Id
    private String deptId;

    @Column(name = "STAFF_DEPT")
    private String staffDept;
}
