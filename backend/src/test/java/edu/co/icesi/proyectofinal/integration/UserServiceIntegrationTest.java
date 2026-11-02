package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.EnrollmentRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;
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
public class UserServiceIntegrationTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserService userService;

    private User userTest;

    @BeforeEach
    public void setup() {
        userTest = new User();
        userTest.setFirstName("Juan");
        userTest.setLastName("Perez");
        userTest.setInstitutionalEmail("juan.perez@icesi.edu.co");
        userTest.setPassword("pass123");
        userRepository.save(userTest);
    }

    @AfterEach
    public void cleanup() {
        enrollmentRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    public void saveUser_WhenValidUser_ShouldPersistAndReturnWithId() {
        // ARRANGE
        User newUser = new User();
        newUser.setFirstName("Maria");
        newUser.setLastName("Lopez");
        newUser.setInstitutionalEmail("maria.lopez@icesi.edu.co");
        newUser.setPassword("pass456");

        // ACT
        User saved = userService.saveUser(newUser);

        // ASSERT
        assertNotNull(saved.getIdUser());
        assertNotNull(userRepository.getUserByIdUser(saved.getIdUser()).orElse(null));
    }

    @Test
    public void getUsers_WhenUsersExist_ShouldReturnAll() {
        // ACT
        List<User> result = userService.getUsers();

        // ASSERT
        assertFalse(result.isEmpty());
        assertTrue(result.size() >= 1);
    }

    @Test
    public void getUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // ARRANGE
        userRepository.deleteAll();

        // ACT
        List<User> result = userService.getUsers();

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @Test
    public void getUserById_WhenExists_ShouldReturnUser() {
        // ACT
        User result = userService.getUserById(userTest.getIdUser());

        // ASSERT
        assertNotNull(result);
        assertEquals("Juan", result.getFirstName());
        assertEquals("Perez", result.getLastName());
    }

    @Test
    public  void getUserById_WhenNotExists_ShouldThrowRuntimeException() {
        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> userService.getUserById(666));
    }


    @Test
    public void deleteUserById_WhenExists_ShouldRemoveUser() {
        // ACT
        userService.deleteUserById(userTest.getIdUser());

        // ASSERT
        assertThrows(RuntimeException.class,
                () -> userService.getUserById(userTest.getIdUser()));
    }

    @Test
    public void getUserByEmail_WhenExists_ShouldReturnUser() {
        // ACT
        User result = userService.getUserByEmail("juan.perez@icesi.edu.co");

        // ASSERT
        assertNotNull(result);
        assertEquals("juan.perez@icesi.edu.co", result.getInstitutionalEmail());
    }

    @Test
    public void getUserByEmail_WhenNotExists_ShouldThrowRuntimeException() {
        // ACT & ASSERT
        assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail("fantasma@icesi.edu.co"));
    }


    @Test
    public void updateUser_WhenExists_ShouldReflectChangesInDB() {
        // ARRANGE
        User updatedData = new User();
        updatedData.setFirstName("Carlos");
        updatedData.setLastName("Gomez");
        updatedData.setInstitutionalEmail("carlos.gomez@icesi.edu.co");
        updatedData.setPassword("newpass");

        // ACT
        userService.updateUser(userTest.getIdUser(), updatedData);
        User result = userService.getUserById(userTest.getIdUser());

        // ASSERT
        assertEquals("Carlos", result.getFirstName());
        assertEquals("carlos.gomez@icesi.edu.co", result.getInstitutionalEmail());
    }









}
