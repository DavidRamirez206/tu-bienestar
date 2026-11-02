package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findBySpaceIdSpace(Integer spaceId);

    List<Activity> findByNameContainingIgnoreCase(String name);

    Optional<Activity> findTopByOrderByStartDateDesc();

    long countBySpaceIdSpace(Integer spaceId);
}
