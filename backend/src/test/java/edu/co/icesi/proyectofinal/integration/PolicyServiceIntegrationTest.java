package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.repository.PolicyRepository;
import edu.co.icesi.proyectofinal.services.PolicyService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class PolicyServiceIntegrationTest {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyRepository policyRepository;

    private Policy policy;

    @BeforeEach
    void setUp() {

        policy = new Policy();
        policy.setName("TEST_POLICY");
        policy.setDescription("desc");
        policy.setResource("RESOURCE");
        policy.setAction("READ");
        policy.setRolePolicies(new ArrayList<>());
    }

    // -------- savePolicy --------
    @Test
    void savePolicy_shouldPersistInDatabase() {
        Policy saved = policyService.savePolicy(policy);

        assertNotNull(saved.getId());
        assertEquals("TEST_POLICY", saved.getName());

        List<Policy> all = policyRepository.findAll();
        assertEquals(1, all.size());
    }

    // -------- getPolicyById --------
    @Test
    void getPolicyById_shouldReturnPolicy() {
        Policy saved = policyRepository.save(policy);

        Policy found = policyService.getPolicyById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("TEST_POLICY", found.getName());
    }

    // -------- getPolicyById (exception) --------
    @Test
    void getPolicyById_shouldThrowException_whenNotFound() {
        Long id = 999L;

        try {
            policyService.getPolicyById(id);
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }

    // -------- getPolicies --------
    @Test
    void getPolicies_shouldReturnAllPolicies() {
        policyRepository.save(policy);

        Policy second = new Policy();
        second.setName("SECOND_POLICY");
        second.setDescription("desc");
        second.setResource("RESOURCE");
        second.setAction("READ");

        policyRepository.save(second);

        List<Policy> policies = policyService.getPolicies();

        assertEquals(2, policies.size());
    }

    // -------- deletePolicyById --------
    @Test
    void deletePolicyById_shouldRemovePolicy() {
        Policy saved = policyRepository.save(policy);

        policyService.deletePolicyById(saved.getId());

        assertTrue(policyRepository.findAll().isEmpty());
    }

    // -------- updatePolicy --------
    @Test
    void updatePolicy_shouldUpdateDataInDatabase() {
        Policy saved = policyRepository.save(policy);

        Policy updated = new Policy();
        updated.setName("UPDATED");
        updated.setDescription("NEW_DESC");
        updated.setResource("NEW_RESOURCE");
        updated.setAction("WRITE");

        Policy result = policyService.updatePolicy(saved.getId(), updated);

        assertEquals("UPDATED", result.getName());
        assertEquals("NEW_DESC", result.getDescription());
        assertEquals("NEW_RESOURCE", result.getResource());
        assertEquals("WRITE", result.getAction());
    }

    // -------- updatePolicy (exception) --------
    @Test
    void updatePolicy_shouldThrowException_whenNotFound() {
        try {
            policyService.updatePolicy(999L, new Policy());
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }
}