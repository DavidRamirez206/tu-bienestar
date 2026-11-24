package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import edu.co.icesi.proyectofinal.repository.RolePolicyRepository;
import edu.co.icesi.proyectofinal.services.impl.RolePolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolePolicyServiceTest {

    @Mock
    private RolePolicyRepository rolePolicyRepository;

    @InjectMocks
    private RolePolicyServiceImpl rolePolicyService;

    private RolePolicy rolePolicy;
    private RolePoliciesId id;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        Policy policy = new Policy();
        policy.setId(2L);
        policy.setName("READ");

        id = new RolePoliciesId();
        id.setRoleId(1L);
        id.setPolicyId(2L);

        rolePolicy = new RolePolicy();
        rolePolicy.setId(id);
        rolePolicy.setRole(role);
        rolePolicy.setPolicy(policy);
    }

    @Test
    void save_shouldReturnSaved() {
        when(rolePolicyRepository.save(rolePolicy)).thenReturn(rolePolicy);

        RolePolicy result = rolePolicyService.save(rolePolicy);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(rolePolicyRepository).save(rolePolicy);
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(rolePolicyRepository).deleteById(id);

        rolePolicyService.deleteById(id);

        verify(rolePolicyRepository).deleteById(id);
    }

    @Test
    void getById_whenExists_shouldReturn() {
        when(rolePolicyRepository.findById(id)).thenReturn(Optional.of(rolePolicy));

        RolePolicy result = rolePolicyService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        when(rolePolicyRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> rolePolicyService.getById(id));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void findAll_shouldReturnList() {
        when(rolePolicyRepository.findAll()).thenReturn(List.of(rolePolicy));

        List<RolePolicy> result = rolePolicyService.findAll();

        assertEquals(1, result.size());
        verify(rolePolicyRepository).findAll();
    }

    @Test
    void getByRoleId_shouldReturnFilteredList() {
        when(rolePolicyRepository.findByRoleId(1L)).thenReturn(List.of(rolePolicy));

        List<RolePolicy> result = rolePolicyService.getByRoleId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getByRoleId_whenNoMatch_shouldReturnEmptyList() {
        when(rolePolicyRepository.findByRoleId(99L)).thenReturn(List.of());

        assertTrue(rolePolicyService.getByRoleId(99L).isEmpty());
    }

    @Test
    void getByPolicyId_shouldReturnFilteredList() {
        when(rolePolicyRepository.findByPolicyId(2L)).thenReturn(List.of(rolePolicy));

        List<RolePolicy> result = rolePolicyService.getByPolicyId(2L);

        assertEquals(1, result.size());
    }

    @Test
    void existsByRoleId_whenExists_shouldReturnTrue() {
        when(rolePolicyRepository.existsByRoleId(1L)).thenReturn(true);

        assertTrue(rolePolicyService.existsByRoleId(1L));
    }

    @Test
    void existsByRoleId_whenNotExists_shouldReturnFalse() {
        when(rolePolicyRepository.existsByRoleId(99L)).thenReturn(false);

        assertFalse(rolePolicyService.existsByRoleId(99L));
    }

    @Test
    void existsByPolicyId_whenExists_shouldReturnTrue() {
        when(rolePolicyRepository.existsByPolicyId(2L)).thenReturn(true);

        assertTrue(rolePolicyService.existsByPolicyId(2L));
    }

    @Test
    void existsByPolicyId_whenNotExists_shouldReturnFalse() {
        when(rolePolicyRepository.existsByPolicyId(99L)).thenReturn(false);

        assertFalse(rolePolicyService.existsByPolicyId(99L));
    }

    @Test
    void countByRoleId_shouldReturnCount() {
        when(rolePolicyRepository.countByRoleId(1L)).thenReturn(3L);

        assertEquals(3L, rolePolicyService.countByRoleId(1L));
    }
}