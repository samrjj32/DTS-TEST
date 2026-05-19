package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should support full task lifecycle")
    void taskLifecycle() throws Exception {
        String dueDate = LocalDateTime.now().plusDays(3).withNano(0).toString();
        String createBody = """
            {
              "title": "Integration task",
              "description": "Created in test",
              "status": "PENDING",
              "dueDateTime": "%s"
            }
            """.formatted(dueDate);

        MvcResult createResult = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Integration task"))
            .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long id = created.get("id").asLong();

        mockMvc.perform(get("/api/tasks/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING"));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(patch("/api/tasks/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"IN_PROGRESS\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(delete("/api/tasks/" + id))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tasks/" + id))
            .andExpect(status().isNotFound());
    }
}
