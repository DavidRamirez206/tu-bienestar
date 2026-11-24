package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.User;

import java.util.List;

public interface AssignmentService {
    Assignment assign(Integer userId, Integer trainerId);
    Assignment reassign(Integer userId, Integer trainerId);
    void unassign(Integer userId, Integer trainerId);
    Assignment findById(Integer userId, Integer trainerId);
    List<Assignment> findAll();
    List<Assignment> findByUserId(Integer userId);
    List<Assignment> findByTrainerId(Integer trainerId);
    List<User> findUsersByRole(String roleName);
}
