package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, AssignmentId> {

    List<Assignment> findByUserAssignmentIdUser(Integer userId);

    List<Assignment> findByTrainerAssignmentIdUser(Integer trainerId);

    boolean existsByUserAssignmentIdUser(Integer userId);

    long countByTrainerAssignmentIdUser(Integer trainerId);
}
