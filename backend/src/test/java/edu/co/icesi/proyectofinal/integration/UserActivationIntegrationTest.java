package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.security.SessionCookie;
import edu.co.icesi.proyectofinal.services.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU9.1 Desactivar un entrenador sin borrarlo. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserActivationIntegrationTest {

    private static final String TRAINER_EMAIL = "pedro.entrenador@icesi.edu.co";
    private static final String PASSWORD = "clave-de-prueba";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;

    private User trainer;

    @BeforeEach
    void setUp() {
        trainer = create("Pedro", TRAINER_EMAIL);
    }

    private User create(String firstName, String email) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName("Prueba");
        user.setInstitutionalEmail(email);
        user.setPassword(PASSWORD);
        user.setUserRoles(new ArrayList<>());
        return userService.saveUser(user);
    }

    private RequestPostProcessor as(String email, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(email)
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private RequestPostProcessor asAdmin() {
        return as("admin.activacion@icesi.edu.co", "ADMIN", "EDIT_USER", "LIST_USER");
    }

    private void setActive(RequestPostProcessor who, boolean active, int expectedStatus) throws Exception {
        mockMvc.perform(put("/api/v1/users/" + trainer.getIdUser() + "/active").with(who)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\":" + active + "}"))
                .andExpect(status().is(expectedStatus));
    }

    // ---------- Escenario: el administrador desactiva al entrenador

    @Test
    void newUsersStartActive() {
        assertTrue(userService.getUserById(trainer.getIdUser()).isActive());
    }

    @Test
    void adminDeactivatesAndReactivatesTheTrainer() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + trainer.getIdUser() + "/active").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUser").value(trainer.getIdUser()))
                .andExpect(jsonPath("$.active").value(false));
        assertFalse(userService.getUserById(trainer.getIdUser()).isActive());

        setActive(asAdmin(), true, 200);
        assertTrue(userService.getUserById(trainer.getIdUser()).isActive());
    }

    // ---------- Escenario: un inactivo no entra

    @Test
    void deactivatedUserCannotLogIn() throws Exception {
        String body = "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(TRAINER_EMAIL, PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        setActive(asAdmin(), false, 200);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void theSessionOfADeactivatedUserStopsWorking() throws Exception {
        String body = "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(TRAINER_EMAIL, PASSWORD);
        Cookie session = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andReturn().getResponse().getCookie(SessionCookie.NAME);

        mockMvc.perform(get("/api/v1/auth/me").cookie(session)).andExpect(status().isOk());

        setActive(asAdmin(), false, 200);

        mockMvc.perform(get("/api/v1/auth/me").cookie(session)).andExpect(status().isUnauthorized());
    }

    // ---------- Permisos y validaciones

    @Test
    void onlyUsersWithEditUserCanChangeTheState() throws Exception {
        setActive(as(TRAINER_EMAIL, "TRAINER", "VIEW_OWN_PROFILE"), false, 403);
        assertTrue(userService.getUserById(trainer.getIdUser()).isActive());
    }

    @Test
    void theStateIsRequired() throws Exception {
        mockMvc.perform(put("/api/v1/users/" + trainer.getIdUser() + "/active").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
