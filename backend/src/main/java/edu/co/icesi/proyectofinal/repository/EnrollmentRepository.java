package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Enrollment;
import edu.co.icesi.proyectofinal.entity.keys.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {

    List<Enrollment> findByUserIdUser(Integer userId);

    List<Enrollment> findByActivityIdActivity(Integer activityId);

    List<Enrollment> findByActivityName(String activityName);

    boolean existsByUserIdUser(Integer userId);

    long countByActivityIdActivity(Integer activityId);
}
