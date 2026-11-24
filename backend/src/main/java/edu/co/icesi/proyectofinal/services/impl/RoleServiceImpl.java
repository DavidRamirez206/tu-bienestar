package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import edu.co.icesi.proyectofinal.repository.PolicyRepository;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.services.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PolicyRepository policyRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PolicyRepository policyRepository){
        this.roleRepository = roleRepository;
        this.policyRepository = policyRepository;
    }

    public Role saveRole(Role role){
        return roleRepository.save(role);
    }

    public void deleteRoleById(Long roleId){

        if(!roleRepository.existsById(roleId)){
            throw new IllegalArgumentException("Role with id "+
                    roleId +" not found");
        }

        roleRepository.deleteById(roleId);

    }



    public Role getRoleById(Long roleId){
        return roleRepository.getRoleById(roleId)
                .orElseThrow(() ->
                        new RuntimeException("Role with id " + roleId
                                + " not found"));
    }

    public List<Role> getRoles(){
        return roleRepository.findAll();

    }

    public List<Role> getRolesByPolicesName(String policyName){
        return roleRepository.getRolesByRolePolicies_Policy_Name(policyName);

    }

    //Esto hay que revisar que funcione como debe.
    @Transactional
    public Role updateRole(Long roleId, String name, List<Long> policyIds) {

        Role existing = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existing.setName(name);

        assignPoliciesToRole(existing, policyIds);

        return roleRepository.save(existing);
    }

    @Transactional
    public void assignPolicies(Long roleId, List<Long> policyIds){

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));

        assignPoliciesToRole(role, policyIds);

        roleRepository.save(role);
    }

    private void assignPoliciesToRole(Role role, List<Long> policyIds) {

        if (role.getRolePolicies() == null) {
            role.setRolePolicies(new ArrayList<>());
        } else {
            role.getRolePolicies().clear();
        }

        if (policyIds != null) {
            for (Long policyId : policyIds) {

                Policy policy = policyRepository.findById(policyId)
                        .orElseThrow(() -> new RuntimeException("Policy not found"));


                RolePoliciesId id = new RolePoliciesId();
                id.setRoleId(role.getId());
                id.setPolicyId(policy.getId());

                RolePolicy rp = new RolePolicy();
                rp.setId(id);
                rp.setRole(role);
                rp.setPolicy(policy);

                role.getRolePolicies().add(rp);
            }
        }
    }

}
