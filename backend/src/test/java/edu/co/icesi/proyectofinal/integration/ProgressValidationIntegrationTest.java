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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProgressValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private void expectRejected(String body, String field) throws Exception {
        mockMvc.perform(post("/api/v1/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString(field)));
    }

    @Test
    @WithMockUser(username = "ana.gomez@u.icesi.edu.co", authorities = "CREATE_PROGRESS")
    void negativeRepsAreRejected() throws Exception {
        expectRejected("{\"exerciseId\":1,\"reps\":-50,\"effortLevel\":5}", "reps");
    }

    @Test
    @WithMockUser(username = "ana.gomez@u.icesi.edu.co", authorities = "CREATE_PROGRESS")
    void negativeWeightAndDurationAreRejected() throws Exception {
        expectRejected("{\"exerciseId\":1,\"weightKg\":-10,\"effortLevel\":5}", "weightKg");
        expectRejected("{\"exerciseId\":1,\"durationMin\":-5,\"effortLevel\":5}", "durationMin");
    }

    @Test
    @WithMockUser(username = "ana.gomez@u.icesi.edu.co", authorities = "CREATE_PROGRESS")
    void effortLevelOutOfRangeIsRejected() throws Exception {
        expectRejected("{\"exerciseId\":1,\"reps\":10,\"effortLevel\":99}", "effortLevel");
    }

    @Test
    @WithMockUser(username = "ana.gomez@u.icesi.edu.co", authorities = "CREATE_PROGRESS")
    void missingExerciseIsRejected() throws Exception {
        expectRejected("{\"reps\":10,\"effortLevel\":5}", "exerciseId");
    }
}
