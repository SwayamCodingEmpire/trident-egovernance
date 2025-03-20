package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "STAFFROLE")
@Table(name = "STAFFROLE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StaffRoleEntity {

    @Id
    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "ROLE_NAME")
    private String roleName;
}
