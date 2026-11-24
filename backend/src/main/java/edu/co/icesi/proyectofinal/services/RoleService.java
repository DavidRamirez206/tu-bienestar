package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Role;

import java.util.List;

public interface RoleService {

    Role saveRole(Role role);

    void deleteRoleById(Long roleId);

    Role getRoleById(Long roleId);

    List<Role> getRoles();

    List<Role> getRolesByPolicesName(String policyName);

    Role updateRole(Long roleId, String name, List<Long> policyIds);

    void assignPolicies(Long roleId, List<Long> policyIds);
}
