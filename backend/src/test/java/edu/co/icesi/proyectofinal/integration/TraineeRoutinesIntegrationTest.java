package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.AssignmentRepository;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.repository.UserRoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU5 Ver rutinas y progreso de los usuarios asignados. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TraineeRoutinesIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private RoutineRepository routineRepository;
    @Autowired private ProgressRepository progressRepository;
    @Autowired private EntityManager entityManager;

    private User juan;
    private User maria;
    private User trainerOne;
    private User trainerTwo;
    private User admin;

    @BeforeEach
    void setUp() {
        juan = user("juan.rutinas@u.icesi.edu.co", "TRAINEE");
        maria = user("maria.rutinas@u.icesi.edu.co", "TRAINEE");
        trainerOne = user("trainer.rutinas1@icesi.edu.co", "TRAINER");
        trainerTwo = user("trainer.rutinas2@icesi.edu.co", "TRAINER");
        admin = user("admin.rutinas@icesi.edu.co", "ADMIN");

        assign(juan, trainerOne);
        routine(juan, "Fuerza de Juan");
        routine(maria, "Cardio de Maria");
        progress(juan, 12);
        progress(maria, 30);

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

    private void assign(User user, User trainer) {
        AssignmentId id = new AssignmentId();
        id.setUserId(user.getIdUser());
        id.setTrainerId(trainer.getIdUser());
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setUserAssignment(user);
        assignment.setTrainerAssignment(trainer);
        assignment.setAssignmentDate(LocalDate.now());
        assignmentRepository.save(assignment);
    }

    private void routine(User owner, String name) {
        Routine routine = new Routine();
        routine.setUserRoutine(owner);
        routine.setName(name);
        routine.setDescription("Rutina de prueba");
        routine.setCreationDate(LocalDate.now());
        routine.setPredesigned(false);
        routineRepository.save(routine);
    }

    private void progress(User owner, int reps) {
        Progress entry = new Progress();
        entry.setUserProgress(owner);
        entry.setDateLogged(LocalDateTime.now());
        entry.setReps(reps);
        progressRepository.save(entry);
    }

    private RequestPostProcessor as(User user, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private RequestPostProcessor asTrainer(User trainer) {
        return as(trainer, "TRAINER", "LIST_ROUTINE");
    }

    // ---------- Escenario: el entrenador ve las rutinas de su alumno

    @Test
    void trainerSeesTheRoutinesOfTheAssignedTrainee() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + juan.getIdUser()).with(asTrainer(trainerOne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Fuerza de Juan"))
                .andExpect(jsonPath("$[0].userId").value(juan.getIdUser()));
    }

    // ---------- Escenario: sin asignacion no hay acceso

    @Test
    void trainerCannotSeeTheRoutinesOfSomeoneElsesTrainee() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + maria.getIdUser()).with(asTrainer(trainerOne)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/routines/user/" + juan.getIdUser()).with(asTrainer(trainerTwo)))
                .andExpect(status().isForbidden());
    }

    @Test
    void traineeCannotSeeTheRoutinesOfAnotherTrainee() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + maria.getIdUser())
                        .with(as(juan, "TRAINEE", "LIST_ROUTINE")))
                .andExpect(status().isForbidden());
    }

    // ---------- Escenario: cada uno ve lo suyo y el administrador ve todo

    @Test
    void traineeSeesOwnRoutines() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + juan.getIdUser())
                        .with(as(juan, "TRAINEE", "LIST_ROUTINE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void adminSeesTheRoutinesOfAnyUser() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + maria.getIdUser())
                        .with(as(admin, "ADMIN", "LIST_ROUTINE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cardio de Maria"));
    }

    // ---------- Escenario: detalle del asignado, con su progreso

    @Test
    void trainerSeesTheProgressOfTheAssignedTrainee() throws Exception {
        mockMvc.perform(get("/api/v1/progress/user/" + juan.getIdUser())
                        .with(as(trainerOne, "TRAINER", "LIST_PROGRESS")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reps").value(12))
                .andExpect(jsonPath("$[0].userId").value(juan.getIdUser()));
    }

    @Test
    void trainerCannotSeeTheProgressOfSomeoneElsesTrainee() throws Exception {
        mockMvc.perform(get("/api/v1/progress/user/" + maria.getIdUser())
                        .with(as(trainerOne, "TRAINER", "LIST_PROGRESS")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/progress/user/" + juan.getIdUser())
                        .with(as(trainerTwo, "TRAINER", "LIST_PROGRESS")))
                .andExpect(status().isForbidden());
    }

    @Test
    void traineeSeesOwnProgressButNotSomeoneElses() throws Exception {
        mockMvc.perform(get("/api/v1/progress/user/" + juan.getIdUser())
                        .with(as(juan, "TRAINEE", "LIST_PROGRESS")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/progress/user/" + maria.getIdUser())
                        .with(as(juan, "TRAINEE", "LIST_PROGRESS")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminSeesTheProgressOfAnyUser() throws Exception {
        mockMvc.perform(get("/api/v1/progress/user/" + maria.getIdUser())
                        .with(as(admin, "ADMIN", "LIST_PROGRESS")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reps").value(30));
    }

    @Test
    void routinesByUserRequireTheListAuthority() throws Exception {
        mockMvc.perform(get("/api/v1/routines/user/" + juan.getIdUser()).with(as(trainerOne, "TRAINER")))
                .andExpect(status().isForbidden());
    }
}
