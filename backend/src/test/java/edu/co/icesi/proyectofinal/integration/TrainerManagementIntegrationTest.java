package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.User;
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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU9.1 Panel administrativo: gestionar entrenadores. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrainerManagementIntegrationTest {

    private static final String EMAIL = "nuevo.entrenador@icesi.edu.co";
    private static final String PASSWORD = "clave-de-prueba";

    private static final String ADMIN_EMAIL = "admin.entrenadores@icesi.edu.co";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;

    /** El administrador tiene que existir en la base: los endpoints lo buscan por su correo. */
    @BeforeEach
    void createAdmin() {
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("Prueba");
        admin.setInstitutionalEmail(ADMIN_EMAIL);
        admin.setPassword(PASSWORD);
        admin.setUserRoles(new ArrayList<>());
        userService.saveUserWithRole(admin, "ADMIN");
    }

    private RequestPostProcessor asAdmin() {
        return as(ADMIN_EMAIL, "ADMIN", "LIST_USER", "CREATE_USER", "EDIT_USER");
    }

    private RequestPostProcessor as(String email, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(email)
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private String body(String firstName, String email) {
        return ("{\"firstName\":\"%s\",\"lastName\":\"Prueba\",\"institutionalEmail\":\"%s\","
                + "\"age\":30,\"password\":\"%s\"}").formatted(firstName, email, PASSWORD);
    }

    private int createTrainer(String firstName, String email) throws Exception {
        String response = mockMvc.perform(post("/api/v1/users/trainers").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(firstName, email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.institutionalEmail").value(email))
                .andExpect(jsonPath("$.active").value(true))
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.idUser");
    }

    // ---------- Escenario: Crear y editar entrenador

    @Test
    void adminCreatesATrainerAndTheDataIsStored() throws Exception {
        int id = createTrainer("Pedro", EMAIL);

        mockMvc.perform(get("/api/v1/users/" + id).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Pedro"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void theNewTrainerHasTheTrainerRole() throws Exception {
        createTrainer("Pedro", EMAIL);

        Cookie session = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getCookie("mb_session");

        mockMvc.perform(get("/api/v1/auth/me").cookie(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities", hasItem("TRAINER")));
    }

    @Test
    void adminEditsTheTrainerData() throws Exception {
        int id = createTrainer("Pedro", EMAIL);

        mockMvc.perform(put("/api/v1/users/" + id).with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Pedro Jose\",\"age\":31}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Pedro Jose"))
                .andExpect(jsonPath("$.age").value(31))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void anEmailOutsideTheUniversityIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/users/trainers").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Pedro", "pedro@gmail.com")))
                .andExpect(status().isBadRequest());
    }

    // ---------- Escenario: Ver estado de los entrenadores

    @Test
    void theTrainerListShowsTheStateOfEachOne() throws Exception {
        int activo = createTrainer("Activo", "activo.entrenador@icesi.edu.co");
        int inactivo = createTrainer("Inactivo", "inactivo.entrenador@icesi.edu.co");

        mockMvc.perform(put("/api/v1/users/" + inactivo + "/active").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\":false}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/users/trainers").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.idUser == " + activo + ")].active", hasItem(true)))
                .andExpect(jsonPath("$[?(@.idUser == " + inactivo + ")].active", hasItem(false)));
    }

    // ---------- Permisos

    @Test
    void onlyAdministratorsManageTrainers() throws Exception {
        mockMvc.perform(post("/api/v1/users/trainers").with(as("juan@u.icesi.edu.co", "TRAINEE", "VIEW_OWN_PROFILE"))
                        .contentType(MediaType.APPLICATION_JSON).content(body("Pedro", EMAIL)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/users/trainers").with(as("juan@u.icesi.edu.co", "TRAINEE", "VIEW_OWN_PROFILE")))
                .andExpect(status().isForbidden());
    }
}
