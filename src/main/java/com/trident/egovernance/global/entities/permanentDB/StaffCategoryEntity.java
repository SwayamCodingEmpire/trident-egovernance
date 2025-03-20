package com.trident.egovernance.global.entities.permanentDB;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "STAFFCATEGORY")
@Table(name = "STAFFCATEGORY")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StaffCategoryEntity {

    @Id
    @Column(name = "CAT_ID")
    private String catId;

    @Column(name = "CATEGORY")
    private String category;
}
