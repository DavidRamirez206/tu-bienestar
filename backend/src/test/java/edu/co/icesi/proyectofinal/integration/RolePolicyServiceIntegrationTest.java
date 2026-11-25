package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import edu.co.icesi.proyectofinal.repository.PolicyRepository;
import edu.co.icesi.proyectofinal.repository.RolePolicyRepository;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.services.RolePolicyService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class RolePolicyServiceIntegrationTest {

    @Autowired
    private RolePolicyService rolePolicyService;

    @Autowired
    private RolePolicyRepository rolePolicyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private Role role;
    private Policy policy;
    private RolePoliciesId id;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        policy = new Policy();
        policy.setName("READ_USERS");
        policy.setResource("users");
        policy.setAction("READ");
        policyRepository.save(policy);

        id = new RolePoliciesId();
        id.setRoleId(role.getId());
        id.setPolicyId(policy.getId());
    }

    private RolePolicy buildRolePolicy() {
        RolePolicy rp = new RolePolicy();
        rp.setId(id);
        rp.setRole(role);
        rp.setPolicy(policy);
        return rp;
    }

    @Test
    void save_shouldPersist() {
        RolePolicy saved = rolePolicyService.save(buildRolePolicy());

        assertNotNull(saved.getId());
        assertEquals(1, rolePolicyRepository.findAll().size());
    }

    @Test
    void getById_whenExists_shouldReturn() {
        rolePolicyRepository.save(buildRolePolicy());

        RolePolicy result = rolePolicyService.getById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        RolePoliciesId missing = new RolePoliciesId();
        missing.setRoleId(999L);
        missing.setPolicyId(999L);

        assertThrows(RuntimeException.class, () -> rolePolicyService.getById(missing));
    }

    @Test
    void deleteById_shouldRemove() {
        rolePolicyRepository.save(buildRolePolicy());

        rolePolicyService.deleteById(id);

        assertTrue(rolePolicyRepository.findAll().isEmpty());
    }

    @Test
    void findAll_shouldReturnAll() {
        rolePolicyRepository.save(buildRolePolicy());

        assertEquals(1, rolePolicyService.findAll().size());
    }

    @Test
    void getByRoleId_shouldReturnFiltered() {
        rolePolicyRepository.save(buildRolePolicy());

        List<RolePolicy> result = rolePolicyService.getByRoleId(role.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getByPolicyId_shouldReturnFiltered() {
        rolePolicyRepository.save(buildRolePolicy());

        List<RolePolicy> result = rolePolicyService.getByPolicyId(policy.getId());

        assertEquals(1, result.size());
    }

    @Test
    void existsByRoleId_whenExists_shouldReturnTrue() {
        rolePolicyRepository.save(buildRolePolicy());

        assertTrue(rolePolicyService.existsByRoleId(role.getId()));
    }

    @Test
    void existsByPolicyId_whenExists_shouldReturnTrue() {
        rolePolicyRepository.save(buildRolePolicy());

        assertTrue(rolePolicyService.existsByPolicyId(policy.getId()));
    }

    @Test
    void countByRoleId_shouldReturnCorrectCount() {
        rolePolicyRepository.save(buildRolePolicy());

        assertEquals(1L, rolePolicyService.countByRoleId(role.getId()));
    }
}