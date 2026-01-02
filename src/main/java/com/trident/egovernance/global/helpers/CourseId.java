package com.trident.egovernance.global.helpers;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CourseId implements Serializable {
    private String course;
    private String studentType;
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(o == null || getClass()!=o.getClass()){
            return false;
        }
        CourseId courseId = (CourseId)o;
        return course.equals(courseId.course) && studentType.equals(courseId.studentType);
    }

    @Override
    public int hashCode(){
        return Objects.hash(course, studentType);
    }

}
