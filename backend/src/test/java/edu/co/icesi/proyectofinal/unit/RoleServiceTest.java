package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.services.impl.RoleServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
    }

    // -------- saveRole --------
    @Test
    void saveRole_shouldReturnSavedRole() {
        when(roleRepository.save(role)).thenReturn(role);

        Role result = roleService.saveRole(role);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).save(role);
    }

    // -------- deleteRoleById --------
    @Test
    void deleteRoleById_shouldCallRepository() {
        when(roleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(1L);
        roleService.deleteRoleById(1L);
        verify(roleRepository).deleteById(1L);
    }

    // -------- getRoleById (success) --------
    @Test
    void getRoleById_shouldReturnRole_whenExists() {
        when(roleRepository.getRoleById(1L))
                .thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(roleRepository).getRoleById(1L);
    }

    // -------- getRoleById (not found) --------
    @Test
    void getRoleById_shouldThrowException_whenNotFound() {
        when(roleRepository.getRoleById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roleService.getRoleById(1L));

        assertTrue(ex.getMessage().contains("not found"));
    }

    // -------- getRoles --------
    @Test
    void getRoles_shouldReturnList() {
        List<Role> roles = Arrays.asList(role, new Role());

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getRoles();

        assertEquals(2, result.size());
        verify(roleRepository).findAll();
    }

    // -------- getRolesByPolicesName --------
    @Test
    void getRolesByPolicesName_shouldReturnFilteredRoles() {
        String policyName = "READ_POLICY";

        List<Role> roles = List.of(role);

        when(roleRepository.getRolesByRolePolicies_Policy_Name(policyName))
                .thenReturn(roles);

        List<Role> result = roleService.getRolesByPolicesName(policyName);

        assertEquals(1, result.size());
        verify(roleRepository)
                .getRolesByRolePolicies_Policy_Name(policyName);
    }

    // -------- updateRole (success) --------
    @Test
    void updateRole_shouldModifyAndSaveRole() {
        Role updated = new Role();
        updated.setName("USER");


        // Simular listas (pueden ser null también si tu entidad lo permite)
        role.setRolePolicies(new ArrayList<>());
        role.setUserRoles(new ArrayList<>());
        updated.setRolePolicies(new ArrayList<>());
        updated.setUserRoles(new ArrayList<>());

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));
        when(roleRepository.save(role))
                .thenReturn(role);

        List<Long> policyIds = new ArrayList<>();

        Role result = roleService.updateRole(1L, updated.getName(),policyIds);

        assertEquals("USER", result.getName());
        assertEquals(updated.getRolePolicies(), result.getRolePolicies());
        assertEquals(updated.getUserRoles(), result.getUserRoles());
        assertTrue(result.getRolePolicies().isEmpty());

        verify(roleRepository).save(role);
    }

    // -------- updateRole (not found) --------
    @Test
    void updateRole_shouldThrowException_whenRoleNotFound() {
        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> roleService.updateRole(1L, "NotExistentRole", new ArrayList<>()));

        verify(roleRepository, never()).save(any());
    }
}