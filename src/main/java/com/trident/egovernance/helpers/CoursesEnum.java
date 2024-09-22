package com.trident.egovernance.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public enum CoursesEnum {
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
