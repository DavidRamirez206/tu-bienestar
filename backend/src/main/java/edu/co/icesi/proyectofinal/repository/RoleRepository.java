package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    long countByNameContaining(String keyword);

    Optional<Role> findTopByOrderByIdDesc();

    Optional<Role> getRoleById(Long roleId);

    List<Role> getRolesByRolePolicies_Policy_Name(String policyName);
}
