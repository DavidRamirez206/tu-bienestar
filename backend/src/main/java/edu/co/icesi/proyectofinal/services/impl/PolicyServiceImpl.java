package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.repository.PolicyRepository;
import edu.co.icesi.proyectofinal.services.PolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository){
        this.policyRepository = policyRepository;
    }


    public Policy savePolicy(Policy policy){
        return policyRepository.save(policy);
    }

    public void deletePolicyById(Long idPolicy){
        if(!policyRepository.existsById(idPolicy)){
            throw new RuntimeException("Policy with id " +
                    idPolicy + " not found");
        }

        policyRepository.deleteById(idPolicy);

    }

    public Policy getPolicyById(Long idPolicy){
        return policyRepository.getPolicyById(idPolicy)
                .orElseThrow(() -> new RuntimeException("Policy with id " + idPolicy + " not found"));
    }

    public List<Policy> getPolicies(){
        return policyRepository.findAll();

    }


    public List<Policy> getPoliciesByRoleName(String roleName){
        return policyRepository.getPoliciesByRolePolicies_Role_Name(roleName);

    }

    public Policy updatePolicy(Long idPolicy, Policy updatedPolicy) {
        Policy existing = policyRepository.getPolicyById(idPolicy)
                .orElseThrow(() -> new RuntimeException("Policy with id " + idPolicy + " not found"));

        existing.setName(updatedPolicy.getName());
        existing.setDescription(updatedPolicy.getDescription());
        existing.setResource(updatedPolicy.getResource());
        existing.setAction(updatedPolicy.getAction());

        if (existing.getRolePolicies() != null) {
            existing.getRolePolicies().clear();
        } else {
            existing.setRolePolicies(new java.util.ArrayList<>());
        }

        if (updatedPolicy.getRolePolicies() != null) {
            for (RolePolicy rp : updatedPolicy.getRolePolicies()) {
                rp.setPolicy(existing);
                existing.getRolePolicies().add(rp);
            }
        }

        return policyRepository.save(existing);

    }


}
