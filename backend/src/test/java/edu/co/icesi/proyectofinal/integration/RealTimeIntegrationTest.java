package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.Space;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import edu.co.icesi.proyectofinal.repository.AssignmentRepository;
import edu.co.icesi.proyectofinal.repository.MessageRepository;
import edu.co.icesi.proyectofinal.repository.NotificationRepository;
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
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** T5, HU15 y HU16. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RealTimeIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private SpaceRepository spaceRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private MessageRepository messageRepository;
    @Autowired private NotificationRepository notificationRepository;

    private User trainer;
    private User juan;
    private User maria;
    private User other;
    private Space gym;

    @BeforeEach
    void setUp() {
        trainer = user("Tomas", "trainer@icesi.edu.co");
        juan = user("Juan", "juan@u.icesi.edu.co");
        maria = user("Maria", "maria@u.icesi.edu.co");
        other = user("Otro", "otro@u.icesi.edu.co");
        assign(trainer, juan);
        assign(trainer, maria);
        gym = new Space();
        gym.setName("Gimnasio");
        gym.setCapacity(20);
        gym.setLocation("Primer piso");
        gym = spaceRepository.save(gym);
    }

    private User user(String name, String email) {
        User user = new User();
        user.setFirstName(name);
        user.setLastName("Prueba");
        user.setInstitutionalEmail(email);
        user.setPassword("hash");
        return userRepository.save(user);
    }

    private void assign(User trainer, User trainee) {
        AssignmentId id = new AssignmentId();
        id.setTrainerId(trainer.getIdUser());
        id.setUserId(trainee.getIdUser());
        Assignment assignment = new Assignment();
        assignment.setId(id);
        assignment.setTrainerAssignment(trainer);
        assignment.setUserAssignment(trainee);
        assignment.setAssignmentDate(LocalDate.now());
        assignmentRepository.save(assignment);
    }

    private RequestPostProcessor as(User user, String... authorities) {
        return SecurityMockMvcRequestPostProcessors.user(user.getInstitutionalEmail())
                .authorities(Stream.of(authorities).map(SimpleGrantedAuthority::new).toList());
    }

    private RequestPostProcessor asTrainer() {
        return as(trainer, "TRAINER", "VIEW_OWN_PROFILE", "LIST_MESSAGE", "CREATE_MESSAGE", "LIST_NOTIFICATION", "DELETE_NOTIFICATION");
    }

    private RequestPostProcessor asTrainee(User user) {
        return as(user, "TRAINEE", "VIEW_OWN_PROFILE", "LIST_ACTIVITY", "LIST_NOTIFICATION", "DELETE_NOTIFICATION");
    }

    private RequestPostProcessor asAdmin() {
        return as(user("Admin", "admin@icesi.edu.co"), "ADMIN", "CREATE_ACTIVITY", "CREATE_SPACE", "LIST_NOTIFICATION");
    }

    private String messageBody(String content, Integer... receiverIds) {
        return """
                {"receiverIds":%s,"content":%s}
                """.formatted(java.util.Arrays.toString(receiverIds), content == null ? "null" : "\"" + content + "\"");
    }

    // ---------- HU15

    @Test
    void newActivityCreatesPendingNotificationForEveryUser() throws Exception {
        mockMvc.perform(post("/api/v1/activities").with(asAdmin()).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Torneo de squash","description":"Abierto","startDate":"%s","endDate":"%s","spaceId":%d}
                                """.formatted(LocalDate.now().plusDays(3), LocalDate.now().plusDays(3), gym.getIdSpace())))
                .andExpect(status().isCreated());

        // Juan no estaba conectado: al volver a ingresar ve la notificacion pendiente con enlace al detalle.
        mockMvc.perform(get("/api/v1/notifications").param("unreadOnly", "true").with(asTrainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("NEW_ACTIVITY"))
                .andExpect(jsonPath("$[0].message", containsString("Torneo de squash")))
                .andExpect(jsonPath("$[0].referenceType").value("ACTIVITY"))
                .andExpect(jsonPath("$[0].isRead").value(false));
    }

    @Test
    void newSpaceNotifiesUsers() throws Exception {
        mockMvc.perform(post("/api/v1/spaces").with(asAdmin()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Piscina\",\"capacity\":30,\"location\":\"Bloque L\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/notifications").with(asTrainee(maria)))
                .andExpect(jsonPath("$[*].type", hasItem("NEW_SPACE")))
                .andExpect(jsonPath("$[0].referenceType").value("SPACE"));
    }

    @Test
    void markReadAndDeleteOnlyAffectOwnNotifications() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Hola", juan.getIdUser())))
                .andExpect(status().isCreated());
        Integer id = notificationRepository.findByUserTargetIdUser(juan.getIdUser()).get(0).getIdNotification();

        mockMvc.perform(patch("/api/v1/notifications/" + id + "/read").with(asTrainee(maria)))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/notifications/" + id).with(asTrainee(maria)))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/v1/notifications/" + id + "/read").with(asTrainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
        mockMvc.perform(get("/api/v1/notifications").param("unreadOnly", "true").with(asTrainee(juan)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void markAllAsRead() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                .content(messageBody("Uno", juan.getIdUser())));
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                .content(messageBody("Dos", juan.getIdUser())));

        mockMvc.perform(patch("/api/v1/notifications/read-all").with(asTrainee(juan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(2));
    }

    // ---------- HU16

    @Test
    void trainerSendsMessageToSeveralAssignedUsers() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Mañana entrenamos a las 7", juan.getIdUser(), maria.getIdUser())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].senderId").value(trainer.getIdUser()))
                .andExpect(jsonPath("$[0].senderName").value("Tomas Prueba"));

        // Si no estaba conectada, lo encuentra guardado al volver a ingresar.
        mockMvc.perform(get("/api/v1/messages").with(asTrainee(maria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("Mañana entrenamos a las 7"));
        mockMvc.perform(get("/api/v1/notifications").with(asTrainee(maria)))
                .andExpect(jsonPath("$[0].type").value("NEW_MESSAGE"))
                .andExpect(jsonPath("$[0].userSourceName").value("Tomas Prueba"));
    }

    @Test
    void messageToUnassignedUserIsNotSent() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Hola", juan.getIdUser(), other.getIdUser())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("no está asignado")));

        // Ni siquiera al asignado: el envio es todo o nada.
        assertEquals(0, messageRepository.count());
    }

    @Test
    void emptyMessageIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("   ", juan.getIdUser())))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/messages").with(asTrainer()).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Hola")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void traineeWithoutCreateMessageCannotSend() throws Exception {
        mockMvc.perform(post("/api/v1/messages").with(asTrainee(juan)).contentType(MediaType.APPLICATION_JSON)
                        .content(messageBody("Hola", trainer.getIdUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    void contactsComeFromAssignments() throws Exception {
        mockMvc.perform(get("/api/v1/messages/contacts").with(asTrainer()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Juan"))
                .andExpect(jsonPath("$[0].relation").value("TRAINEE"));
        mockMvc.perform(get("/api/v1/messages/contacts").with(asTrainee(juan)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].relation").value("TRAINER"));
        mockMvc.perform(get("/api/v1/messages/contacts").with(asTrainee(other)))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ---------- T5

    @Test
    void webSocketEndpointRequiresSession() throws Exception {
        mockMvc.perform(get("/api/ws")).andExpect(status().isUnauthorized());
    }
}
