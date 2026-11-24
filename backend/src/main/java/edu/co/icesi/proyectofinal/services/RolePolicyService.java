package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;

import java.util.List;

public interface RolePolicyService {

    RolePolicy save(RolePolicy rolePolicy);

    void deleteById(RolePoliciesId id);

    RolePolicy getById(RolePoliciesId id);

    List<RolePolicy> findAll();

    List<RolePolicy> getByRoleId(Long roleId);

    List<RolePolicy> getByPolicyId(Long policyId);

    boolean existsByRoleId(Long roleId);

    boolean existsByPolicyId(Long policyId);

    long countByRoleId(Long roleId);

    RolePolicy update(RolePoliciesId id, RolePolicy updated);
}