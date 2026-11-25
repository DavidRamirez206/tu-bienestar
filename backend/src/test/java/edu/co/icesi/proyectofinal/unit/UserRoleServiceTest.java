package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.UserRoleRepository;
import edu.co.icesi.proyectofinal.services.impl.UserRoleServiceImpl;
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
public class UserRoleServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    private UserRole userRole;
    private UserRoleId id;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setIdUser(1);

        Role role = new Role();
        role.setId(2L);
        role.setName("TRAINER");

        id = new UserRoleId();
        id.setUserId(1);
        id.setRoleId(2L);

        userRole = new UserRole();
        userRole.setId(id);
        userRole.setUser(user);
        userRole.setRole(role);
    }

    @Test
    void save_shouldReturnSaved() {
        when(userRoleRepository.save(userRole)).thenReturn(userRole);

        UserRole result = userRoleService.save(userRole);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(userRoleRepository).save(userRole);
    }

    @Test
    void deleteById_shouldCallRepository() {
        doNothing().when(userRoleRepository).deleteById(id);

        userRoleService.deleteById(id);

        verify(userRoleRepository).deleteById(id);
    }

    @Test
    void getById_whenExists_shouldReturn() {
        when(userRoleRepository.findById(id)).thenReturn(Optional.of(userRole));

        UserRole result = userRoleService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        when(userRoleRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userRoleService.getById(id));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void findAll_shouldReturnList() {
        when(userRoleRepository.findAll()).thenReturn(List.of(userRole));

        List<UserRole> result = userRoleService.findAll();

        assertEquals(1, result.size());
        verify(userRoleRepository).findAll();
    }

    @Test
    void getByUserId_shouldReturnFilteredList() {
        when(userRoleRepository.findByUserIdUser(1)).thenReturn(List.of(userRole));

        List<UserRole> result = userRoleService.getByUserId(1);

        assertEquals(1, result.size());
    }

    @Test
    void getByUserId_whenNoMatch_shouldReturnEmptyList() {
        when(userRoleRepository.findByUserIdUser(99)).thenReturn(List.of());

        assertTrue(userRoleService.getByUserId(99).isEmpty());
    }

    @Test
    void getByRoleId_shouldReturnFilteredList() {
        when(userRoleRepository.findByRoleId(2L)).thenReturn(List.of(userRole));

        List<UserRole> result = userRoleService.getByRoleId(2L);

        assertEquals(1, result.size());
    }

    @Test
    void existsByUserId_whenExists_shouldReturnTrue() {
        when(userRoleRepository.existsByUserIdUser(1)).thenReturn(true);

        assertTrue(userRoleService.existsByUserId(1));
    }

    @Test
    void existsByUserId_whenNotExists_shouldReturnFalse() {
        when(userRoleRepository.existsByUserIdUser(99)).thenReturn(false);

        assertFalse(userRoleService.existsByUserId(99));
    }

    @Test
    void existsByRoleId_whenExists_shouldReturnTrue() {
        when(userRoleRepository.existsByRoleId(2L)).thenReturn(true);

        assertTrue(userRoleService.existsByRoleId(2L));
    }

    @Test
    void existsByRoleId_whenNotExists_shouldReturnFalse() {
        when(userRoleRepository.existsByRoleId(99L)).thenReturn(false);

        assertFalse(userRoleService.existsByRoleId(99L));
    }

    @Test
    void countByRoleId_shouldReturnCount() {
        when(userRoleRepository.countByRoleId(2L)).thenReturn(4L);

        assertEquals(4L, userRoleService.countByRoleId(2L));
    }
}