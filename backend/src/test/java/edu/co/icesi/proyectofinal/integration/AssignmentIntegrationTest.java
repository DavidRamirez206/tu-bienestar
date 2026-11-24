package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.AssignmentRepository;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.repository.UserRoleRepository;
import jakarta.persistence.EntityManager;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU9.2 Panel administrativo: asignar entrenadores. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AssignmentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private EntityManager entityManager;

    private User juan;
    private User maria;
    private User trainerOne;
    private User trainerTwo;
    private User admin;

    @BeforeEach
    void setUp() {
        juan = user("juan@u.icesi.edu.co", "TRAINEE");
        maria = user("maria@u.icesi.edu.co", "TRAINEE");
        trainerOne = user("trainer1@icesi.edu.co", "TRAINER");
        trainerTwo = user("trainer2@icesi.edu.co", "TRAINER");
        admin = user("admin@icesi.edu.co", "ADMIN");
        entityManager.flush();
        entityManager.clear();
    }

    private User user(String email, String roleName) {
        User user = new User();
        user.setFirstName("Nombre");
        user.setLastName(roleName);
        user.setInstitutionalEmail(email);
        user.setPassword("hash");
        user = userRepository.save(user);
        Role role = roleRepository.findAll().stream()
                .filter(candidate -> roleName.equals(candidate.getName()))
                .findFirst().orElseThrow();
        UserRoleId id = new UserRoleId();
        id.setUserId(user.getIdUser());
        id.setRoleId(role.getId());
        UserRole userRole = new UserRole();
        userRole.setId(id);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        user.setUserRoles(new ArrayList<>());
        return user;
    }

    private RequestPostProcessor as(User user, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private RequestPostProcessor asAdmin() {
        return as(admin, "ADMIN", "LIST_ASSIGNMENT", "CREATE_ASSIGNMENT", "DELETE_ASSIGNMENT");
    }

    private RequestPostProcessor asTrainer(User trainer) {
        return as(trainer, "TRAINER", "LIST_ASSIGNMENT", "CREATE_ASSIGNMENT");
    }

    private String body(User user, User trainer) {
        return "{\"userId\":" + user.getIdUser() + ",\"trainerId\":" + trainer.getIdUser() + "}";
    }

    private void assign(User user, User trainer) throws Exception {
        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(user, trainer)))
                .andExpect(status().isCreated());
    }

    // ---------- Escenario: Asignar entrenador

    @Test
    void assignedUserAppearsInTrainerStudents() throws Exception {
        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerOne)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(juan.getIdUser()))
                .andExpect(jsonPath("$.trainerId").value(trainerOne.getIdUser()))
                .andExpect(jsonPath("$.userEmail").value(juan.getInstitutionalEmail()));

        mockMvc.perform(get("/api/v1/assignments/me").with(asTrainer(trainerOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(juan.getIdUser()));
    }

    // ---------- Escenario: Reasignar entrenador

    @Test
    void reassignMovesUserToNewTrainerAndOldTrainerStopsSeeingIt() throws Exception {
        assign(juan, trainerOne);

        mockMvc.perform(put("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerTwo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerId").value(trainerTwo.getIdUser()));

        mockMvc.perform(get("/api/v1/assignments/me").with(asTrainer(trainerTwo)))
                .andExpect(jsonPath("$[*].userId", hasItem(juan.getIdUser())));
        mockMvc.perform(get("/api/v1/assignments/me").with(asTrainer(trainerOne)))
                .andExpect(jsonPath("$[*].userId", not(hasItem(juan.getIdUser()))));
        assertEquals(1, assignmentRepository.findByUserAssignmentIdUser(juan.getIdUser()).size());
    }

    @Test
    void reassigningUserWithoutTrainerAnswers404() throws Exception {
        mockMvc.perform(put("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(maria, trainerTwo)))
                .andExpect(status().isNotFound());
    }

    // ---------- Escenario: Asignacion duplicada

    @Test
    void duplicateAssignmentIsRejectedAndNotCreated() throws Exception {
        assign(juan, trainerOne);

        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerOne)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("ya está asignado")));
        mockMvc.perform(put("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerOne)))
                .andExpect(status().isConflict());

        assertEquals(1, assignmentRepository.findByUserAssignmentIdUser(juan.getIdUser()).size());
    }

    @Test
    void userWithTrainerMustBeReassignedInsteadOfAssignedAgain() throws Exception {
        assign(juan, trainerOne);
        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerTwo)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("reasignar")));
    }

    // ---------- Validaciones y permisos

    @Test
    void onlyTrainersCanBeAssigned() throws Exception {
        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, maria)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("no es entrenador")));
        mockMvc.perform(post("/api/v1/assignments").with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"userId\":" + juan.getIdUser() + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void trainerCannotManageAssignments() throws Exception {
        mockMvc.perform(post("/api/v1/assignments").with(asTrainer(trainerOne))
                        .contentType(MediaType.APPLICATION_JSON).content(body(juan, trainerOne)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/assignments/trainers").with(asTrainer(trainerOne)))
                .andExpect(status().isForbidden());
    }

    @Test
    void traineeCannotListOtherTrainerStudents() throws Exception {
        assign(juan, trainerOne);
        mockMvc.perform(get("/api/v1/assignments/trainer/" + trainerOne.getIdUser())
                        .with(as(maria, "TRAINEE", "LIST_ASSIGNMENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void trainerOnlySeesOwnStudentsInGeneralList() throws Exception {
        assign(juan, trainerOne);
        assign(maria, trainerTwo);
        mockMvc.perform(get("/api/v1/assignments").with(asTrainer(trainerOne)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(juan.getIdUser()));
        mockMvc.perform(get("/api/v1/assignments").with(asAdmin()))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void adminListsTrainersAndTrainees() throws Exception {
        mockMvc.perform(get("/api/v1/assignments/trainers").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/v1/assignments/trainees").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].idUser", hasItem(juan.getIdUser())))
                .andExpect(jsonPath("$[*].idUser", not(hasItem(trainerOne.getIdUser()))));
    }

    @Test
    void adminRemovesAssignment() throws Exception {
        assign(juan, trainerOne);
        mockMvc.perform(delete("/api/v1/assignments/" + juan.getIdUser() + "/" + trainerOne.getIdUser()).with(asAdmin()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/assignments/me").with(asTrainer(trainerOne)))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
