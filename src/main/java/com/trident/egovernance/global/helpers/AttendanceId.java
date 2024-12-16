package com.trident.egovernance.global.helpers;

import java.util.Objects;

public class AttendanceId {
    private String regdNo;
    private String subAbbr;

    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        AttendanceId attendanceId = (AttendanceId)o;
        return Objects.equals(regdNo,attendanceId.regdNo) && Objects.equals(subAbbr,attendanceId.subAbbr);
    }

    @Override
    public int hashCode(){
        return Objects.hash(regdNo,subAbbr);
    }
}
