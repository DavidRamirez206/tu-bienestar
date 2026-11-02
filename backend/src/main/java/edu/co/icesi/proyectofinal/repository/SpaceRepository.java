package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Integer> {

    List<Space> findByCapacityGreaterThanEqual(int capacity);

    Optional<Space> findTopByOrderByCapacityDesc();

    Optional<Space> findByName(String name);
}
