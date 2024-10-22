package com.trident.egovernance.global.services;

import com.trident.egovernance.global.entities.permanentDB.Course;
import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.repositories.permanentDB.CourseRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CourseFetchingServiceImpl {
    private final CourseRepository courseRepository;

    public CourseFetchingServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Cacheable(value = "course", key = "#course")
    public Course getCourseDetails(Courses course){
        return courseRepository.findById(course.getDisplayName()).orElseThrow(()-> new RuntimeException("Course not found"));
    }
}
