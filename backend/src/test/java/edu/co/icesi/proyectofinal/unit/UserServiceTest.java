package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setIdUser(1);
        user.setFirstName("Juan");
        user.setLastName("Perez");
        user.setInstitutionalEmail("juan.perez@icesi.edu.co");
        user.setPassword("pass123");
    }


    @Test
    void saveUser_WhenValidUser_ShouldReturnSavedUser() {
        // ARRANGE
        when(
                encoder.encode(anyString())
        ).thenReturn("hashedPassword");

        when(
                userRepository.save(user)
        ).thenReturn(
                user
        );

        // ACT
        User result = userService.saveUser(user);

        // ASSERT
        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getInstitutionalEmail(), result.getInstitutionalEmail());
    }



    @Test
    void deleteUserById_WhenExists_ShouldNotThrowException() {
        when(userRepository.existsById(1)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1);
        assertDoesNotThrow(() -> userService.deleteUserById(1));
        verify(userRepository).deleteById(1);
    }



    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        // ARRANGE
        when(
                userRepository.getUserByIdUser(1)
        ).thenReturn(
                Optional.of(user)
        );

        // ACT
        User result = userService.getUserById(1);

        // ASSERT
        assertNotNull(result);
        assertEquals(user.getIdUser(), result.getIdUser());
        assertEquals(user.getFirstName(), result.getFirstName());
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowRuntimeException() {
        // ARRANGE
        when(
                userRepository.getUserByIdUser(99)
        ).thenReturn(
                Optional.empty()
        );

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> userService.getUserById(99));
    }

    @Test
    void getUsers_WhenUsersExist_ShouldReturnList() {
        // ARRANGE
        when(
                userRepository.findAll()
        ).thenReturn(
                List.of(user)
        );

        // ACT
        List<User> result = userService.getUsers();

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // ARRANGE
        when(
                userRepository.findAll()
        ).thenReturn(
                List.of()
        );

        // ACT
        List<User> result = userService.getUsers();

        // ASSERT
        assertTrue(result.isEmpty());
    }


    @Test
    void getUsersByFirstNameIgnoreCase_WhenMatchExists_ShouldReturnUsers() {
        // ARRANGE
        when(
                userRepository.findByFirstNameContainingIgnoreCase("juan")
        ).thenReturn(
                List.of(user)
        );

        // ACT
        List<User> result = userService.getUsersByFirstNameIgnoreCase("juan");

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(user.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    void getUsersByFirstNameIgnoreCase_WhenNoMatch_ShouldReturnEmptyList() {
        // ARRANGE
        when(
                userRepository.findByFirstNameContainingIgnoreCase("xyz")
        ).thenReturn(
                List.of()
        );

        // ACT
        List<User> result = userService.getUsersByFirstNameIgnoreCase("xyz");

        // ASSERT
        assertTrue(result.isEmpty());
    }


    @Test
    void getUserByLastName_WhenMatchExists_ShouldReturnUsers() {
        // ARRANGE
        when(
                userRepository.findByLastName("Perez")
        ).thenReturn(
                List.of(user)
        );

        // ACT
        List<User> result = userService.getUserByLastName("Perez");

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(user.getLastName(), result.get(0).getLastName());
    }


    @Test
    void getUserByEmail_WhenExists_ShouldReturnUser() {
        // ARRANGE
        when(
                userRepository.findByInstitutionalEmail("juan.perez@icesi.edu.co")

        ).thenReturn(
                Optional.of(user)
        );

        // ACT
        User result = userService.getUserByEmail("juan.perez@icesi.edu.co");

        // ASSERT
        assertNotNull(result);
        assertEquals(user.getInstitutionalEmail(), result.getInstitutionalEmail());
    }

    @Test
    void getUserByEmail_WhenNotExists_ShouldThrowRuntimeException() {
        // ARRANGE
        when(
                userRepository.findByInstitutionalEmail("noexiste@icesi.edu.co")
        ).thenReturn(
                        Optional.empty()
        );

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail("noexiste@icesi.edu.co"));
    }


    @Test
    void getByFirstAndLastName_WhenExists_ShouldReturnUser() {
        // ARRANGE
        when(userRepository.findByFirstNameAndLastName("Juan", "Perez"))
                .thenReturn(Optional.of(user));

        // ACT
        User result = userService.getByFirstAndLastName("Juan", "Perez");

        // ASSERT
        assertNotNull(result);
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
    }

    @Test
    void getByFirstAndLastName_WhenNotExists_ShouldThrowRuntimeException() {
        // ARRANGE
        when(
                userRepository.findByFirstNameAndLastName("X", "Y")
        ).thenReturn(
                Optional.empty()
        );

        // ACT & ASSERT
        assertThrows(RuntimeException.class,
                () -> userService.getByFirstAndLastName("X", "Y"));
    }


    @Test
    void updateUser_WhenExists_ShouldUpdateAndReturnUser() {
        // ARRANGE
        User updatedData = new User();
        updatedData.setFirstName("Carlos");
        updatedData.setLastName("Gomez");
        updatedData.setInstitutionalEmail("carlos@icesi.edu.co");
        updatedData.setPassword("newpass");

        when(
                userRepository.getUserByIdUser(1)
        ).thenReturn(
                Optional.of(user)
        );

        // Cuando se llame a .save va a devolver el mismo User que recibió como argumento
        when(
                userRepository.save(any(User.class))
        ).thenAnswer(
                x-> x.getArgument(0)
        );


        // ACT
        User result = userService.updateUser(1, updatedData);

        // ASSERT
        assertEquals("Carlos", result.getFirstName());
        assertEquals("Gomez", result.getLastName());
        assertEquals("carlos@icesi.edu.co", result.getInstitutionalEmail());
    }


    @Test
    void updateUser_WhenNotExists_ShouldThrowRuntimeException() {
        // ARRANGE
        when(
                userRepository.getUserByIdUser(99)
        ).thenReturn(
                Optional.empty()
        );

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> userService.updateUser(99, new User()));
    }
}
