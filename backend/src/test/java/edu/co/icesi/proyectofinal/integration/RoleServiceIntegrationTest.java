package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.services.RoleService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {

        role = createValidRole("ADMIN");
    }

    // -------- helper --------
    private Role createValidRole(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }

    // -------- saveRole --------
    @Test
    void saveRole_shouldPersistInDatabase() {
        Role saved = roleService.saveRole(role);

        assertNotNull(saved.getId());
        assertEquals("ADMIN", saved.getName());

        assertEquals(1, roleRepository.findAll().size());
    }

    // -------- getRoleById --------
    @Test
    @WithMockUser(authorities = "ASSIGN_POLICY")
    void getRoleById_shouldReturnRole() {
        Role saved = roleRepository.save(role);

        Role found = roleService.getRoleById(saved.getId());

        assertEquals(saved.getId(), found.getId());
        assertEquals("ADMIN", found.getName());
    }

    // -------- getRoleById (exception) --------
    @Test
    void getRoleById_shouldThrowException_whenNotFound() {
        try {
            roleService.getRoleById(999L);
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }

    // -------- getRoles --------
    @Test
    void getRoles_shouldReturnAllRoles() {
        roleRepository.save(createValidRole("ADMIN"));
        roleRepository.save(createValidRole("USER"));

        List<Role> roles = roleService.getRoles();

        assertEquals(2, roles.size());
    }

    // -------- deleteRoleById --------
    @Test
    void deleteRoleById_shouldRemoveRole() {
        Role saved = roleRepository.save(role);

        roleService.deleteRoleById(saved.getId());

        assertTrue(roleRepository.findAll().isEmpty());
    }

    // -------- updateRole --------
    @Test
    void updateRole_shouldUpdateData() {
        Role saved = roleRepository.save(role);

        Role updated = new Role();
        updated.setName("USER");

        // importante: inicializar listas si existen constraints
        updated.setRolePolicies(null);
        updated.setUserRoles(null);

        List<Long> policyIds = new ArrayList<>();

        Role result = roleService.updateRole(saved.getId(), updated.getName(),policyIds );

        assertEquals("USER", result.getName());
    }

    // -------- updateRole (exception) --------
    @Test
    void updateRole_shouldThrowException_whenNotFound() {
        try {
            roleService.updateRole(999L, "roleNotExistent", new ArrayList<>());
            fail("Expected exception not thrown");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }
}