package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Policy;

import java.util.List;

public interface PolicyService {
    Policy savePolicy(Policy policy);

    void deletePolicyById(Long idPolicy);

    Policy getPolicyById(Long idPolicy);

    List<Policy> getPolicies();

    List<Policy> getPoliciesByRoleName(String roleName);

    Policy updatePolicy(Long idPolicy, Policy updatedPolicy);


}
