package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Schedule;
import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ActivityRepository;
import edu.co.icesi.proyectofinal.repository.ScheduleRepository;
import edu.co.icesi.proyectofinal.repository.SpaceRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU8.2 Eventos y espacios: horarios y tipo. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ScheduleIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private SpaceRepository spaceRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private ScheduleRepository scheduleRepository;

    private User juan;
    private Space gym;
    private Activity yoga;
    private Activity tournament;
    private Activity openGym;

    @BeforeEach
    void setUp() {
        juan = user("juan@u.icesi.edu.co");
        gym = new Space();
        gym.setName("Gimnasio");
        gym.setCapacity(20);
        gym.setLocation("Primer piso");
        gym = spaceRepository.save(gym);
        yoga = activity("Yoga", "CLASE");
        tournament = activity("Torneo", "TORNEO");
        openGym = activity("Gimnasio libre", "GIMNASIO");
        schedule(yoga, "TUESDAY", "07:00", "08:00");
        schedule(yoga, "MONDAY", "18:00", "19:00");
        schedule(openGym, "MONDAY", "06:00", "21:00");
        schedule(tournament, "SATURDAY", "09:00", "13:00");
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Nombre");
        user.setLastName("Apellido");
        user.setInstitutionalEmail(email);
        user.setPassword("hash");
        return userRepository.save(user);
    }

    private Activity activity(String name, String type) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setDescription("Descripcion de " + name);
        activity.setType(type);
        activity.setStartDate(LocalDate.now());
        activity.setEndDate(LocalDate.now().plusDays(30));
        activity.setSpace(gym);
        return activityRepository.save(activity);
    }

    private Schedule schedule(Activity activity, String day, String start, String end) {
        Schedule schedule = new Schedule();
        schedule.setActivity(activity);
        schedule.setDayOfWeek(day);
        schedule.setStartTime(LocalTime.parse(start));
        schedule.setEndTime(LocalTime.parse(end));
        return scheduleRepository.save(schedule);
    }

    private RequestPostProcessor trainee() {
        return as(juan, "LIST_ACTIVITY", "LIST_SCHEDULE");
    }

    private RequestPostProcessor admin() {
        return as(user("admin@u.icesi.edu.co"), "ADMIN", "LIST_ACTIVITY", "CREATE_ACTIVITY", "EDIT_ACTIVITY",
                "LIST_SCHEDULE", "CREATE_SCHEDULE", "EDIT_SCHEDULE", "DELETE_SCHEDULE");
    }

    private RequestPostProcessor as(User user, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private String scheduleBody(Integer activityId, String day, String start, String end) {
        return """
                {"activityId":%s,"dayOfWeek":%s,"startTime":%s,"endTime":%s}
                """.formatted(activityId,
                day == null ? "null" : "\"" + day + "\"",
                start == null ? "null" : "\"" + start + "\"",
                end == null ? "null" : "\"" + end + "\"");
    }

    // ---------- Escenario: Ver horarios de un evento

    @Test
    void eventDetailShowsSchedulesWithDayStartAndEnd() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/activity/" + yoga.getIdActivity()).with(trainee()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                // Ordenados por dia de la semana: lunes antes que martes.
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("18:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("19:00:00"))
                .andExpect(jsonPath("$[1].dayOfWeek").value("TUESDAY"))
                .andExpect(jsonPath("$[0].activityName").value("Yoga"));
    }

    @Test
    void schedulesOfUnknownEventAnswer400() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/activity/999999").with(trainee()))
                .andExpect(status().isBadRequest());
    }

    // ---------- Escenario: Horarios por dia

    @Test
    void choosingADayShowsOnlyThatDaySortedByTime() throws Exception {
        mockMvc.perform(get("/api/v1/schedules").param("day", "MONDAY").with(trainee()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].dayOfWeek", everyItem(is("MONDAY"))))
                .andExpect(jsonPath("$[0].activityName").value("Gimnasio libre"))
                .andExpect(jsonPath("$[1].activityName").value("Yoga"))
                .andExpect(jsonPath("$[0].spaceName").value("Gimnasio"));
    }

    @Test
    void dayWithoutSchedulesIsEmptyAndInvalidDayIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/schedules").param("day", "SUNDAY").with(trainee()))
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/v1/schedules").param("day", "LUNES").with(trainee()))
                .andExpect(status().isBadRequest());
    }

    // ---------- Escenario: Filtrar por tipo

    @Test
    void filteringByTypeOnlyReturnsThatType() throws Exception {
        mockMvc.perform(get("/api/v1/activities").param("type", "TORNEO").with(trainee()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Torneo"))
                .andExpect(jsonPath("$[0].type").value("TORNEO"));
        mockMvc.perform(get("/api/v1/activities").param("type", "clase").with(trainee()))
                .andExpect(jsonPath("$[*].type", everyItem(is("CLASE"))))
                .andExpect(jsonPath("$[*].name", not(hasItem("Torneo"))));
        mockMvc.perform(get("/api/v1/activities").param("type", "fiesta").with(trainee()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void schedulesCanAlsoBeFilteredByType() throws Exception {
        mockMvc.perform(get("/api/v1/schedules").param("day", "MONDAY").param("type", "GIMNASIO").with(trainee()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].activityType").value("GIMNASIO"));
    }

    @Test
    void eventKeepsTheTypeChosenByAdmin() throws Exception {
        RequestPostProcessor admin = admin();
        mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Copa","description":"Algo","type":"torneo","startDate":"2030-01-01","endDate":"2030-01-02","spaceId":%d}
                                """.formatted(gym.getIdSpace())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TORNEO"));
        mockMvc.perform(post("/api/v1/activities").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Sin tipo","description":"Algo","startDate":"2030-01-01","endDate":"2030-01-02","spaceId":%d}
                                """.formatted(gym.getIdSpace())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("CLASE"));
    }

    // ---------- Administracion de horarios

    @Test
    void adminCreatesEditsAndDeletesSchedule() throws Exception {
        RequestPostProcessor admin = admin();
        String created = mockMvc.perform(post("/api/v1/schedules").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(tournament.getIdActivity(), "sunday", "10:00", "12:00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek").value("SUNDAY"))
                .andReturn().getResponse().getContentAsString();
        Integer id = com.jayway.jsonpath.JsonPath.read(created, "$.idSchedule");

        mockMvc.perform(put("/api/v1/schedules/" + id).with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(tournament.getIdActivity(), "SUNDAY", "10:00", "14:00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endTime").value("14:00:00"));

        mockMvc.perform(delete("/api/v1/schedules/" + id).with(admin))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/v1/schedules/" + id).with(trainee()))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidScheduleIsRejected() throws Exception {
        RequestPostProcessor admin = admin();
        mockMvc.perform(post("/api/v1/schedules").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(yoga.getIdActivity(), "MONDAY", "10:00", "09:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("hora de fin")));
        mockMvc.perform(post("/api/v1/schedules").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(yoga.getIdActivity(), null, null, "09:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("día")));
        mockMvc.perform(post("/api/v1/schedules").with(admin).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(999999, "MONDAY", "08:00", "09:00")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void regularUserCannotManageSchedules() throws Exception {
        mockMvc.perform(post("/api/v1/schedules").with(trainee()).contentType(MediaType.APPLICATION_JSON)
                        .content(scheduleBody(yoga.getIdActivity(), "MONDAY", "08:00", "09:00")))
                .andExpect(status().isForbidden());
    }
}
