package com.trident.egovernance.global.helpers;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
public enum Courses implements Serializable {
    BTECH("B.TECH."),
    MTECH("M.TECH."),
    BCA("BCA"),
    MCA("MCA"),
    BBA("BBA"),
    MBA("MBA");
    private final String displayName;

    public static Courses fromDisplayName(String displayName) {
        for (Courses course : Courses.values()) {
            if (course.getDisplayName().equals(displayName)) {
                return course;
            }
        }
        return null;
    }
    public String getEnumName() {
        return this.name();
    }
    Courses(String displayName) {
        this.displayName = displayName;
    }
}
