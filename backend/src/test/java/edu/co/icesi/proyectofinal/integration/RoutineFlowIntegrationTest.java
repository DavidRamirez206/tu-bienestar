package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
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

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoutineFlowIntegrationTest {

    private static final String[] TRAINEE_AUTHORITIES = {
            "LIST_ROUTINE", "CREATE_ROUTINE", "EDIT_ROUTINE", "DELETE_ROUTINE", "LIST_EXERCISE",
            "LIST_ROUTINE_EXERCISE", "CREATE_ROUTINE_EXERCISE", "EDIT_ROUTINE_EXERCISE", "DELETE_ROUTINE_EXERCISE"
    };

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ExerciseRepository exerciseRepository;
    @Autowired private RoutineRepository routineRepository;

    private User owner;
    private User other;
    private Exercise squats;
    private Exercise pushups;

    @BeforeEach
    void setUp() {
        owner = user("owner@u.icesi.edu.co");
        other = user("other@u.icesi.edu.co");
        squats = exercise("Squats");
        pushups = exercise("Pushups");
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Nombre");
        user.setLastName("Apellido");
        user.setInstitutionalEmail(email);
        user.setPassword("hash");
        return userRepository.save(user);
    }

    private Exercise exercise(String name) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setType("Fuerza");
        exercise.setPredefined(true);
        return exerciseRepository.save(exercise);
    }

    private Routine routine(User routineOwner) {
        Routine routine = new Routine();
        routine.setName("Mi rutina");
        routine.setCreationDate(LocalDate.now());
        routine.setUserRoutine(routineOwner);
        return routineRepository.save(routine);
    }

    private RequestPostProcessor as(User user) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail()).authorities(
                java.util.Arrays.stream(TRAINEE_AUTHORITIES)
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .toList());
    }

    private String items(Object... values) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < values.length; i += 3) {
            if (i > 0) {
                json.append(',');
            }
            json.append("{\"exerciseId\":%s,\"sets\":%s,\"targetReps\":%s}".formatted(values[i], values[i + 1], values[i + 2]));
        }
        return json.append(']').toString();
    }

    @Test
    void createdRoutineBelongsToTheUserAndAppearsInTheList() throws Exception {
        mockMvc.perform(post("/api/v1/routines").with(as(owner)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Piernas\",\"isPredesigned\":true,\"userId\":" + other.getIdUser() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(owner.getIdUser()))
                .andExpect(jsonPath("$.isPredesigned").value(false));

        mockMvc.perform(get("/api/v1/routines").with(as(owner)))
                .andExpect(jsonPath("$[*].name", hasItem("Piernas")));
    }

    @Test
    void routineWithoutNameIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/routines").with(as(owner)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void customExerciseIsOnlyVisibleToItsOwner() throws Exception {
        mockMvc.perform(post("/api/v1/exercises").with(as(owner)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Mi plancha\",\"type\":\"Core\",\"isPredefined\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isPredefined").value(false))
                .andExpect(jsonPath("$.userId").value(owner.getIdUser()));

        mockMvc.perform(get("/api/v1/exercises").with(as(owner)))
                .andExpect(jsonPath("$[*].name", hasItem("Mi plancha")));
        mockMvc.perform(get("/api/v1/exercises").with(as(other)))
                .andExpect(jsonPath("$[*].name", not(hasItem("Mi plancha"))))
                .andExpect(jsonPath("$[*].name", hasItem("Squats")));
    }

    @Test
    void ownerCanAddChangeAndRemoveExercises() throws Exception {
        Integer id = routine(owner).getIdRoutine();
        String url = "/api/v1/routine-exercises/routine/" + id + "/exercises";

        mockMvc.perform(put(url).with(as(owner)).contentType(MediaType.APPLICATION_JSON)
                        .content(items(squats.getIdExercise(), 3, 10, pushups.getIdExercise(), 4, 12)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(put(url).with(as(owner)).contentType(MediaType.APPLICATION_JSON)
                        .content(items(squats.getIdExercise(), 5, 20)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sets").value(5))
                .andExpect(jsonPath("$[0].exerciseName").value("Squats"));
    }

    @Test
    void invalidRepetitionsAreRejected() throws Exception {
        Integer id = routine(owner).getIdRoutine();
        mockMvc.perform(put("/api/v1/routine-exercises/routine/" + id + "/exercises").with(as(owner))
                        .contentType(MediaType.APPLICATION_JSON).content(items(squats.getIdExercise(), 0, 10)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void otherUsersCannotViewOrModifyARoutine() throws Exception {
        Integer id = routine(owner).getIdRoutine();

        mockMvc.perform(get("/api/v1/routines/" + id).with(as(other))).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/routines/" + id).with(as(other)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Robada\"}")).andExpect(status().isForbidden());
        mockMvc.perform(put("/api/v1/routine-exercises/routine/" + id + "/exercises").with(as(other))
                .contentType(MediaType.APPLICATION_JSON).content(items())).andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/v1/routines/" + id).with(as(other))).andExpect(status().isForbidden());
    }
}
