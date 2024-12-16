package com.trident.egovernance.global.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SemesterResultId {
    private String regdNo;
    private int semester;
    private String subjectCode;

    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        SemesterResultId semesterResultId = (SemesterResultId)o;
        return Objects.equals(regdNo,semesterResultId.regdNo) && Objects.equals(semester,semesterResultId.semester) && Objects.equals(subjectCode,semesterResultId.subjectCode);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,semester,subjectCode);
    }
}
