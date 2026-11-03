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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthLoginIntegrationTest {

    private static final String EMAIL = "ana.gomez@u.icesi.edu.co";
    private static final String PASSWORD = "clave-de-prueba";
    private static final String CREDENTIALS_ERROR = "Usuario o contraseña incorrectos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void createUser() {
        User user = new User();
        user.setFirstName("Ana");
        user.setLastName("Gomez");
        user.setInstitutionalEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setUserRoles(new ArrayList<>());
        userService.saveUser(user);
    }

    private ResultActions login(String username, String password) throws Exception {
        String body = "{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password);
        return mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void validCredentialsReturnTokenAndUser() throws Exception {
        login(EMAIL, PASSWORD)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.user.institutionalEmail").value(EMAIL));
    }

    @Test
    void wrongPasswordIsRejectedWithGenericMessage() throws Exception {
        login(EMAIL, "otra-clave")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(CREDENTIALS_ERROR));
    }

    @Test
    void unknownUserGetsTheSameResponseAsWrongPassword() throws Exception {
        login("nadie@u.icesi.edu.co", PASSWORD)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(CREDENTIALS_ERROR));
    }

    @Test
    void protectedRouteRequiresToken() throws Exception {
        mockMvc.perform(get("/api/v1/ruta-protegida")).andExpect(status().isUnauthorized());
    }

    @Test
    void malformedTokenIsRejectedWithoutServerError() throws Exception {
        mockMvc.perform(get("/api/v1/ruta-protegida").header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginStoresTheTokenInAnHttpOnlyCookie() throws Exception {
        login(EMAIL, PASSWORD)
                .andExpect(cookie().httpOnly(SessionCookie.NAME, true))
                .andExpect(cookie().sameSite(SessionCookie.NAME, "Strict"))
                .andExpect(cookie().path(SessionCookie.NAME, "/api"))
                .andExpect(cookie().maxAge(SessionCookie.NAME, 3600));
    }

    @Test
    void cookieIdentifiesTheSession() throws Exception {
        Cookie session = login(EMAIL, PASSWORD).andReturn().getResponse().getCookie(SessionCookie.NAME);

        mockMvc.perform(get("/api/v1/auth/me").cookie(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.institutionalEmail").value(EMAIL))
                .andExpect(jsonPath("$.authorities").isArray());
    }

    @Test
    void sessionIsRejectedWithoutACookieOrWithAnInvalidOne() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/v1/auth/me").cookie(new Cookie(SessionCookie.NAME, "no-es-un-token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRemovesTheCookieEvenWithAnInvalidToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout").cookie(new Cookie(SessionCookie.NAME, "no-es-un-token")))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge(SessionCookie.NAME, 0))
                .andExpect(cookie().value(SessionCookie.NAME, ""));
    }
}
