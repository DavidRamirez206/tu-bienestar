package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU8.1, HU19 y HU9.3. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ActivityEnrollmentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private SpaceRepository spaceRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ExerciseRepository exerciseRepository;
    @Autowired private ProgressRepository progressRepository;
    @Autowired private edu.co.icesi.proyectofinal.repository.RoutineRepository routineRepository;
    @Autowired private jakarta.persistence.EntityManager entityManager;

    private User juan;
    private User maria;
    private Space gym;
    private Activity past;
    private Activity soon;
    private Activity later;

    @BeforeEach
    void setUp() {
        juan = user("juan@u.icesi.edu.co");
        maria = user("maria@u.icesi.edu.co");
        gym = new Space();
        gym.setName("Gimnasio");
        gym.setCapacity(20);
        gym.setLocation("Primer piso");
        gym = spaceRepository.save(gym);
        LocalDate today = LocalDate.now();
        past = activity("Pasado", today.minusDays(10), today.minusDays(9));
        soon = activity("Pronto", today.plusDays(2), today.plusDays(2));
        later = activity("Despues", today.plusDays(30), today.plusDays(31));
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Nombre");
        user.setLastName("Apellido");
        user.setInstitutionalEmail(email);
        user.setPassword("hash");
        return userRepository.save(user);
    }

    private Activity activity(String name, LocalDate start, LocalDate end) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setDescription("Descripcion de " + name);
        activity.setStartDate(start);
        activity.setEndDate(end);
        activity.setSpace(gym);
        return activityRepository.save(activity);
    }

    private RequestPostProcessor trainee(User user) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(java.util.stream.Stream.of("LIST_ACTIVITY", "LIST_SPACE", "LIST_ENROLLMENT",
                                "CREATE_ENROLLMENT", "DELETE_ENROLLMENT", "LIST_EXERCISE")
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new).toList());
    }

    private RequestPostProcessor admin() {
        return SecurityMockMvcRequestPostProcessors.user(user("admin@u.icesi.edu.co").getInstitutionalEmail())
                .authorities(java.util.stream.Stream.of("ADMIN", "LIST_ACTIVITY", "CREATE_ACTIVITY", "EDIT_ACTIVITY",
                                "DELETE_ACTIVITY", "LIST_EXERCISE", "CREATE_EXERCISE", "EDIT_EXERCISE", "DELETE_EXERCISE",
                                "LIST_ENROLLMENT", "CREATE_ENROLLMENT", "DELETE_ENROLLMENT")
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new).toList());
    }

    private String enrollBody(Activity activity) {
        return "{\"activityId\":" + activity.getIdActivity() + "}";
    }

    private String activityBody(String name, String start, String end, Integer spaceId) {
        return """
                {"name":%s,"description":"Algo","startDate":%s,"endDate":%s,"spaceId":%s}
                """.formatted(name == null ? "null" : "\"" + name + "\"",
                start == null ? "null" : "\"" + start + "\"",
                end == null ? "null" : "\"" + end + "\"", spaceId);
    }

    // ---------- HU8.1

    @Test
    void listShowsDateAndPlaceOfEachEvent() throws Exception {
        mockMvc.perform(get("/api/v1/activities/" + soon.getIdActivity()).with(trainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(soon.getStartDate().toString()))
                .andExpect(jsonPath("$.description").value("Descripcion de Pronto"))
                .andExpect(jsonPath("$.spaceName").value("Gimnasio"))
                .andExpect(jsonPath("$.spaceLocation").value("Primer piso"));
    }

    @Test
    void upcomingHidesFinishedEvents() throws Exception {
        mockMvc.perform(get("/api/v1/activities").param("upcoming", "true").with(trainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", not(hasItem("Pasado"))))
                .andExpect(jsonPath("$[*].name", hasItem("Pronto")));
    }

    @Test
    void eventEndingTodayIsStillUpcoming() throws Exception {
        activity("Hoy", LocalDate.now().minusDays(1), LocalDate.now());
        mockMvc.perform(get("/api/v1/activities").param("upcoming", "true").with(trainee(juan)))
                .andExpect(jsonPath("$[*].name", hasItem("Hoy")));
    }

    @Test
    void dateRangeOnlyReturnsEventsInRange() throws Exception {
        LocalDate today = LocalDate.now();
        mockMvc.perform(get("/api/v1/activities")
                        .param("from", today.toString())
                        .param("to", today.plusDays(5).toString())
                        .with(trainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Pronto"));
    }

    @Test
    void invertedOrMalformedRangeIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/activities").param("from", "2030-02-01").param("to", "2030-01-01").with(trainee(juan)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/activities").param("from", "ayer").with(trainee(juan)))
                .andExpect(status().isBadRequest());
    }

    // ---------- HU19

    @Test
    void enrollAndCancelUpdatesMyActivities() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(soon)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(juan.getIdUser()));

        mockMvc.perform(get("/api/v1/enrollments/me").with(trainee(juan)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activity.name").value("Pronto"))
                .andExpect(jsonPath("$[0].activity.spaceName").value("Gimnasio"));

        mockMvc.perform(delete("/api/v1/enrollments/" + juan.getIdUser() + "/" + soon.getIdActivity()).with(trainee(juan)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/enrollments/me").with(trainee(juan)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void duplicateEnrollmentIsRejectedAndNotCreated() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(soon)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(soon)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Ya estás inscrito")));
        mockMvc.perform(get("/api/v1/enrollments/me").with(trainee(juan)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void finishedActivityCannotBeJoined() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(past)))
                .andExpect(status().isConflict());
    }

    @Test
    void cannotActOnSomeoneElsesEnrollments() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(maria))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(soon)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + maria.getIdUser() + ",\"activityId\":" + later.getIdActivity() + "}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/v1/enrollments/user/" + maria.getIdUser()).with(trainee(juan)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/enrollments/" + maria.getIdUser() + "/" + soon.getIdActivity()).with(trainee(juan)))
                .andExpect(status().isForbidden());
        // El listado general solo le devuelve las suyas.
        mockMvc.perform(get("/api/v1/enrollments").with(trainee(juan)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- HU9.3

    @Test
    void adminCreatesEditsAndDeletesEvent() throws Exception {
        RequestPostProcessor admin = admin();
        String created = mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody("Nuevo", "2030-01-01", "2030-01-02", gym.getIdSpace())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.spaceName").value("Gimnasio"))
                .andReturn().getResponse().getContentAsString();
        Integer id = com.jayway.jsonpath.JsonPath.read(created, "$.idActivity");

        mockMvc.perform(get("/api/v1/activities/" + id).with(trainee(juan)))
                .andExpect(jsonPath("$.name").value("Nuevo"));

        mockMvc.perform(put("/api/v1/activities/" + id).with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody("Editado", "2030-01-01", "2030-01-03", gym.getIdSpace())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Editado"));

        mockMvc.perform(delete("/api/v1/activities/" + id).with(admin))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/activities/" + id).with(trainee(juan)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingEventWithEnrollmentsAlsoRemovesThem() throws Exception {
        mockMvc.perform(post("/api/v1/enrollments").with(trainee(juan))
                        .contentType(MediaType.APPLICATION_JSON).content(enrollBody(soon)))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/v1/activities/" + soon.getIdActivity()).with(admin()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/enrollments/me").with(trainee(juan)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void regularUserCannotManageEventsOrExercises() throws Exception {
        mockMvc.perform(post("/api/v1/activities").with(trainee(juan)).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody("Nuevo", "2030-01-01", "2030-01-02", gym.getIdSpace())))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/activities/" + soon.getIdActivity()).with(trainee(juan)))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/exercises/1").with(trainee(juan)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"x\",\"type\":\"y\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void incompleteEventIsRejectedNamingWhatIsMissing() throws Exception {
        RequestPostProcessor admin = admin();
        mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody(null, null, "2030-01-02", gym.getIdSpace())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("nombre")))
                .andExpect(jsonPath("$.message", containsString("fecha de inicio")));
        mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody("X", "2030-01-05", "2030-01-02", gym.getIdSpace())))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(activityBody("X", "2030-01-01", "2030-01-02", 999999)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editingCatalogExerciseKeepsOwnerAndValidates() throws Exception {
        RequestPostProcessor admin = admin();
        Exercise exercise = new Exercise();
        exercise.setName("Plancha");
        exercise.setType("Fuerza");
        exercise.setPredefined(true);
        exercise.setUserExercise(juan);
        exercise = exerciseRepository.save(exercise);

        mockMvc.perform(put("/api/v1/exercises/" + exercise.getIdExercise()).with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Plancha lateral\",\"type\":\"Fuerza\",\"isPredefined\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Plancha lateral"))
                .andExpect(jsonPath("$.userId").value(juan.getIdUser()));

        mockMvc.perform(put("/api/v1/exercises/" + exercise.getIdExercise()).with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"Fuerza\",\"isPredefined\":true}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("nombre")));
    }

    @Test
    void exerciseWithProgressCannotBeDeleted() throws Exception {
        Exercise exercise = new Exercise();
        exercise.setName("Con progreso");
        exercise.setType("Fuerza");
        exercise.setPredefined(true);
        exercise = exerciseRepository.save(exercise);
        Progress progress = new Progress();
        progress.setUserProgress(juan);
        progress.setExercise(exercise);
        progress.setDateLogged(LocalDateTime.now());
        progressRepository.save(progress);

        mockMvc.perform(delete("/api/v1/exercises/" + exercise.getIdExercise()).with(admin()))
                .andExpect(status().isConflict());
    }

    // ---------- HU12

    @Test
    void regularUserOnlySeesHimselfInUserList() throws Exception {
        juan.setUserRoles(new java.util.ArrayList<>());
        mockMvc.perform(get("/api/v1/users").with(SecurityMockMvcRequestPostProcessors.user(juan.getInstitutionalEmail())
                        .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("LIST_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].institutionalEmail").value(juan.getInstitutionalEmail()));
    }

    // ---------- Eliminar rutinas

    private edu.co.icesi.proyectofinal.entity.Routine routineWithProgress(User owner) {
        edu.co.icesi.proyectofinal.entity.Routine routine = new edu.co.icesi.proyectofinal.entity.Routine();
        routine.setName("Rutina");
        routine.setCreationDate(LocalDate.now());
        routine.setUserRoutine(owner);
        routine = routineRepository.save(routine);
        Exercise exercise = new Exercise();
        exercise.setName("Ejercicio");
        exercise.setType("Fuerza");
        exercise.setPredefined(true);
        exercise = exerciseRepository.save(exercise);
        Progress progress = new Progress();
        progress.setUserProgress(owner);
        progress.setExercise(exercise);
        progress.setRoutine(routine);
        progress.setDateLogged(LocalDateTime.now());
        progressRepository.save(progress);
        return routine;
    }

    private RequestPostProcessor routineUser(User user) {
        user.setUserRoles(new java.util.ArrayList<>());
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("DELETE_ROUTINE"));
    }

    @Test
    void ownerDeletesRoutineAndProgressIsKept() throws Exception {
        var routine = routineWithProgress(juan);
        mockMvc.perform(delete("/api/v1/routines/" + routine.getIdRoutine()).with(routineUser(juan)))
                .andExpect(status().isNoContent());
        entityManager.flush();
        entityManager.clear();
        org.junit.jupiter.api.Assertions.assertFalse(routineRepository.existsById(routine.getIdRoutine()));
        var kept = progressRepository.findByUserProgressIdUser(juan.getIdUser());
        org.junit.jupiter.api.Assertions.assertEquals(1, kept.size());
        org.junit.jupiter.api.Assertions.assertNull(kept.get(0).getRoutine());
    }

    @Test
    void otherUserCannotDeleteRoutine() throws Exception {
        var routine = routineWithProgress(juan);
        mockMvc.perform(delete("/api/v1/routines/" + routine.getIdRoutine()).with(routineUser(maria)))
                .andExpect(status().isForbidden());
    }

    // ---------- Rutas inexistentes

    @Test
    void unknownRouteAnswers404() throws Exception {
        mockMvc.perform(get("/api/v1/no-existe").with(trainee(juan)))
                .andExpect(status().isNotFound());
    }
}
