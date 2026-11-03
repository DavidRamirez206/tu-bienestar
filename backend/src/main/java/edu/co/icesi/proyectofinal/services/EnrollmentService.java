package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    Enrollment enroll(Integer userId, Integer activityId);
    void cancel(Integer userId, Integer activityId);
    Enrollment findById(Integer userId, Integer activityId);
    List<Enrollment> findAll();
    List<Enrollment> findByUserId(Integer userId);
}
