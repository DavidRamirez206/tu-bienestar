package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Integer> {

    List<Routine> findByUserRoutineIdUser(Integer userId);

    Optional<Routine> findTopByUserRoutine_IdUser_OrderByCreationDateDesc(Integer userId);

    List<Routine> findByPredesigned(Boolean isPredesigned);

    long countByUserRoutineIdUser(Integer userId);

    boolean existsByNameAndUserRoutineIdUser(String name, Integer userId);
}
