package edu.co.icesi.proyectofinal.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HU6 No se permiten recomendaciones vacias. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecommendationValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private void expectRejected(String body, String field) throws Exception {
        mockMvc.perform(post("/api/v1/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(field)));
    }

    @Test
    @WithMockUser(username = "trainer1@icesi.edu.co", authorities = "CREATE_RECOMMENDATION")
    void emptyDescriptionIsRejected() throws Exception {
        expectRejected("{\"trainerId\":1,\"userId\":2,\"description\":\"\"}", "description");
    }

    @Test
    @WithMockUser(username = "trainer1@icesi.edu.co", authorities = "CREATE_RECOMMENDATION")
    void descriptionWithOnlySpacesIsRejected() throws Exception {
        expectRejected("{\"trainerId\":1,\"userId\":2,\"description\":\"    \"}", "description");
    }

    @Test
    @WithMockUser(username = "trainer1@icesi.edu.co", authorities = "CREATE_RECOMMENDATION")
    void missingDescriptionIsRejected() throws Exception {
        expectRejected("{\"trainerId\":1,\"userId\":2}", "description");
    }

    @Test
    @WithMockUser(username = "trainer1@icesi.edu.co", authorities = "CREATE_RECOMMENDATION")
    void missingUserIsRejected() throws Exception {
        expectRejected("{\"trainerId\":1,\"description\":\"Toma mas agua\"}", "userId");
    }
}
