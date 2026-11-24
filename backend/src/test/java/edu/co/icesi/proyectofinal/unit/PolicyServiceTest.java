package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.repository.PolicyRepository;
import edu.co.icesi.proyectofinal.services.impl.PolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PolicyServiceTest {

    private PolicyRepository policyRepository;
    private PolicyServiceImpl policyService;
    private Policy policy;

    @BeforeEach
    void setUp() {
        policyRepository = mock(PolicyRepository.class);
        policyService = new PolicyServiceImpl(policyRepository);
        policy = new Policy();
        policy.setId(1L);
        policy.setName("READ");
        policy.setRolePolicies(new ArrayList<>());

    }

    @AfterEach
    void tearDown() {
        clearInvocations(policyRepository);
    }

    // -------- savePolicy --------
    @Test
    void savePolicy_shouldReturnSavedPolicy() {
        Policy policy = new Policy();
        policy.setName("Test");

        when(policyRepository.save(policy)).thenReturn(policy);

        Policy result = policyService.savePolicy(policy);

        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(policyRepository).save(policy);
    }

    // -------- deletePolicyById --------
    @Test
    void deletePolicyById_shouldCallRepository() {
        Long id = 1L;

        when(policyRepository.existsById(id)).thenReturn(true);
        doNothing().when(policyRepository).deleteById(id);

        policyService.deletePolicyById(id);

        verify(policyRepository).deleteById(id);
    }

    // -------- getPolicyById (success) --------
    @Test
    void getPolicyById_shouldReturnPolicy_whenExists() {
        Long id = 1L;

        Policy policy = new Policy();
        policy.setId(id);

        when(policyRepository.getPolicyById(id)).thenReturn(Optional.of(policy));

        Policy result = policyService.getPolicyById(id);

        assertEquals(id, result.getId());
        verify(policyRepository).getPolicyById(id);
    }

    // -------- getPolicyById (exception) --------
    @Test
    void getPolicyById_shouldThrowException_whenNotFound() {
        Long id = 1L;

        when(policyRepository.getPolicyById(id)).thenReturn(Optional.empty());

        try {
            policyService.getPolicyById(id);
            fail("Expected exception was not thrown");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }

    // -------- getPolicies --------
    @Test
    void getPolicies_shouldReturnList() {
        List<Policy> policies = Arrays.asList(new Policy(), new Policy());

        when(policyRepository.findAll()).thenReturn(policies);

        List<Policy> result = policyService.getPolicies();

        assertEquals(2, result.size());
        verify(policyRepository).findAll();
    }

    // -------- getPoliciesByRoleName --------
    @Test
    void getPoliciesByRoleName_shouldReturnFilteredPolicies() {
        String roleName = "ADMIN";

        List<Policy> policies = List.of(new Policy());

        when(policyRepository.getPoliciesByRolePolicies_Role_Name(roleName))
                .thenReturn(policies);

        List<Policy> result = policyService.getPoliciesByRoleName(roleName);

        assertEquals(1, result.size());
        verify(policyRepository)
                .getPoliciesByRolePolicies_Role_Name(roleName);
    }

    // -------- updatePolicy (success) --------
    @Test
    void updatePolicy_shouldModifyAndSavePolicy() {
        Long id = 1L;

        Policy existing = new Policy();
        existing.setId(id);
        existing.setName("Old");
        existing.setRolePolicies(new ArrayList<>());

        Policy updated = new Policy();
        updated.setName("New");
        updated.setDescription("Desc");
        updated.setResource("Resource");
        updated.setAction("WRITE");
        updated.setRolePolicies(new ArrayList<>());

        when(policyRepository.getPolicyById(id))
                .thenReturn(Optional.of(existing));
        when(policyRepository.save(existing))
                .thenReturn(existing);

        Policy result = policyService.updatePolicy(id, updated);

        assertEquals("New", result.getName());
        assertEquals("Desc", result.getDescription());
        assertEquals("Resource", result.getResource());
        assertEquals("WRITE", result.getAction());

        verify(policyRepository).save(existing);
    }

    // -------- updatePolicy (not found) --------
    @Test
    void updatePolicy_shouldThrowException_whenPolicyDoesNotExist() {
        Long id = 1L;

        when(policyRepository.getPolicyById(id)).thenReturn(Optional.empty());

        try {
            policyService.updatePolicy(id, new Policy());
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException ex) {
            // puedes validar el mensaje si quieres hacerlo más fuerte
            assertTrue(ex.getMessage().contains("not found"));
        }

        verify(policyRepository, never()).save(any());
    }


    @Test
    void updatePolicy_shouldUpdateRolePolicies() {
        Long id = 1L;

        Policy existing = new Policy();
        existing.setId(id);
        existing.setRolePolicies(new ArrayList<>());

        Policy updated = new Policy();

        List<RolePolicy> rolePolicies = new ArrayList<>();
        updated.setRolePolicies(rolePolicies);

        when(policyRepository.getPolicyById(id))
                .thenReturn(Optional.of(existing));
        when(policyRepository.save(existing))
                .thenReturn(existing);

        Policy result = policyService.updatePolicy(id, updated);

        assertEquals(rolePolicies, result.getRolePolicies());
        verify(policyRepository).save(existing);
    }

    @Test
    void getPolicyById_shouldContainExactMessage() {
        Long id = 1L;

        when(policyRepository.getPolicyById(id)).thenReturn(Optional.empty());

        try {
            policyService.getPolicyById(id);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException ex) {
            assertEquals("Policy with id " + id + " not found", ex.getMessage());
        }
    }
}