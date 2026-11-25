package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import edu.co.icesi.proyectofinal.repository.RolePolicyRepository;
import edu.co.icesi.proyectofinal.services.RolePolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolePolicyServiceImpl implements RolePolicyService {

    private final RolePolicyRepository rolePolicyRepository;

    public RolePolicyServiceImpl(RolePolicyRepository rolePolicyRepository) {
        this.rolePolicyRepository = rolePolicyRepository;
    }

    @Override
    public RolePolicy save(RolePolicy rolePolicy) {
        return rolePolicyRepository.save(rolePolicy);
    }

    @Override
    public void deleteById(RolePoliciesId id) {
        rolePolicyRepository.deleteById(id);
    }

    @Override
    public RolePolicy getById(RolePoliciesId id) {
        return rolePolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "RolePolicy with roleId " + id.getRoleId()
                                + " and policyId " + id.getPolicyId() + " not found"));
    }

    @Override
    public List<RolePolicy> findAll() {
        return rolePolicyRepository.findAll();
    }

    @Override
    public List<RolePolicy> getByRoleId(Long roleId) {
        return rolePolicyRepository.findByRoleId(roleId);
    }

    @Override
    public List<RolePolicy> getByPolicyId(Long policyId) {
        return rolePolicyRepository.findByPolicyId(policyId);
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        return rolePolicyRepository.existsByRoleId(roleId);
    }

    @Override
    public boolean existsByPolicyId(Long policyId) {
        return rolePolicyRepository.existsByPolicyId(policyId);
    }

    @Override
    public long countByRoleId(Long roleId) {
        return rolePolicyRepository.countByRoleId(roleId);
    }

    @Override
    public RolePolicy update(RolePoliciesId id, RolePolicy updated) {
        getById(id);
        updated.setId(id);
        return rolePolicyRepository.save(updated);
    }
}