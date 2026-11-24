package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.repository.UserRoleRepository;
import edu.co.icesi.proyectofinal.services.UserRoleService;
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
class UserRoleServiceIntegrationTest {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;
    private UserRoleId id;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setFirstName("Camila");
        user.setLastName("Suarez");
        user.setInstitutionalEmail("camila.suarez@icesi.edu.co");
        user.setPassword("pass");
        userRepository.save(user);

        role = new Role();
        role.setName("STUDENT");
        roleRepository.save(role);

        id = new UserRoleId();
        id.setUserId(user.getIdUser());
        id.setRoleId(role.getId());
    }

    private UserRole buildUserRole() {
        UserRole ur = new UserRole();
        ur.setId(id);
        ur.setUser(user);
        ur.setRole(role);
        return ur;
    }

    @Test
    void save_shouldPersist() {
        UserRole saved = userRoleService.save(buildUserRole());

        assertNotNull(saved.getId());
        assertEquals(1, userRoleRepository.findAll().size());
    }

    @Test
    void getById_whenExists_shouldReturn() {
        userRoleRepository.save(buildUserRole());

        UserRole result = userRoleService.getById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        UserRoleId missing = new UserRoleId();
        missing.setUserId(999);
        missing.setRoleId(999L);

        assertThrows(RuntimeException.class, () -> userRoleService.getById(missing));
    }

    @Test
    void deleteById_shouldRemove() {
        userRoleRepository.save(buildUserRole());

        userRoleService.deleteById(id);

        assertTrue(userRoleRepository.findAll().isEmpty());
    }

    @Test
    void findAll_shouldReturnAll() {
        userRoleRepository.save(buildUserRole());

        assertEquals(1, userRoleService.findAll().size());
    }

    @Test
    void getByUserId_shouldReturnFiltered() {
        userRoleRepository.save(buildUserRole());

        List<UserRole> result = userRoleService.getByUserId(user.getIdUser());

        assertEquals(1, result.size());
    }

    @Test
    void getByRoleId_shouldReturnFiltered() {
        userRoleRepository.save(buildUserRole());

        List<UserRole> result = userRoleService.getByRoleId(role.getId());

        assertEquals(1, result.size());
    }

    @Test
    void existsByUserId_whenAssigned_shouldReturnTrue() {
        userRoleRepository.save(buildUserRole());

        assertTrue(userRoleService.existsByUserId(user.getIdUser()));
    }

    @Test
    void existsByUserId_whenNotAssigned_shouldReturnFalse() {
        assertFalse(userRoleService.existsByUserId(999));
    }

    @Test
    void existsByRoleId_whenAssigned_shouldReturnTrue() {
        userRoleRepository.save(buildUserRole());

        assertTrue(userRoleService.existsByRoleId(role.getId()));
    }

    @Test
    void countByRoleId_shouldReturnCorrectCount() {
        userRoleRepository.save(buildUserRole());

        assertEquals(1L, userRoleService.countByRoleId(role.getId()));
    }
}