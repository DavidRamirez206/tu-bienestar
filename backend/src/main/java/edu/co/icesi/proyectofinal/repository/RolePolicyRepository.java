package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePolicyRepository extends JpaRepository<RolePolicy, RolePoliciesId> {

    List<RolePolicy> findByRoleId(Long roleId);

    List<RolePolicy> findByPolicyId(Long policyId);

    boolean existsByRoleId(Long roleId);

    boolean existsByPolicyId(Long policyId);

    long countByRoleId(Long roleId);
}
