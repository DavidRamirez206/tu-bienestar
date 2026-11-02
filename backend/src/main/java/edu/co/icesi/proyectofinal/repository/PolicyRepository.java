package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByName(String name);

    boolean existsByName(String name);

    List<Policy> findByResource(String resource);

    List<Policy> findByAction(String action);

    long countByResource(String resource);

    Optional<Policy> findTopByOrderByIdDesc();

    Optional<Policy> getPolicyById(Long idPolicy);

    List<Policy> getPoliciesByRolePolicies_Role_Name(String roleName);

}
