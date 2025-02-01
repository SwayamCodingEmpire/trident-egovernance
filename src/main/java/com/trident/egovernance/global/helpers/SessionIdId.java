package com.trident.egovernance.global.helpers;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SessionIdId implements Serializable {
    private String sessionId;
    private String course;
    private int regdYear;
    private int admissionYear;
    private String studentType;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        SessionIdId sessionIdId = (SessionIdId)o;
        return course.equals(sessionIdId.course) && regdYear==sessionIdId.regdYear && admissionYear==sessionIdId.admissionYear && sessionId.equals(sessionIdId.sessionId) && studentType.equals(sessionIdId.studentType);
    }

    @Override
    public int hashCode(){
        return Objects.hash(course,regdYear,admissionYear, sessionId, studentType);
    }
}
