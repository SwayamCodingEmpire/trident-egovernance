package com.trident.egovernance.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public enum CoursesEnum implements Serializable {
    BTECH("B.TECH."),
    MTECH("M.TECH."),
    BCA("BCA"),
    MCA("MCA"),
    BBA("BBA"),
    MBA("MBA");
    private final String displayName;

    public static CoursesEnum fromDisplayName(String displayName) {
        for (CoursesEnum course : CoursesEnum.values()) {
            if (course.getDisplayName().equals(displayName)) {
                return course;
            }
        }
        return null;
    }
    public String getEnumName() {
        return this.name();
    }
    CoursesEnum(String displayName) {
        this.displayName = displayName;
    }
}
