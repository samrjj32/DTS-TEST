package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.reform.dev.exception.GlobalExceptionHandler;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.models.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    private TaskResponse sampleResponse() {
        return TaskResponse.builder()
            .id(1L)
            .title("Review documents")
            .description("Notes")
            .status(TaskStatus.PENDING)
            .dueDateTime(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("POST /api/tasks creates a task")
    void createTask() throws Exception {
        when(taskService.createTask(any())).thenReturn(sampleResponse());

        String body = """
            {
              "title": "Review documents",
              "description": "Notes",
              "status": "PENDING",
              "dueDateTime": "%s"
            }
            """.formatted(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Review documents"));
    }

    @Test
    @DisplayName("POST /api/tasks returns 400 for invalid payload")
    void createTaskValidationFailure() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns a task")
    void getTask() throws Exception {
        when(taskService.getTask(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/tasks returns all tasks")
    void getAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Review documents"));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/status updates status")
    void updateStatus() throws Exception {
        TaskResponse updated = sampleResponse();
        updated = TaskResponse.builder()
            .id(updated.getId())
            .title(updated.getTitle())
            .description(updated.getDescription())
            .status(TaskStatus.COMPLETED)
            .dueDateTime(updated.getDueDateTime())
            .createdAt(updated.getCreatedAt())
            .updatedAt(updated.getUpdatedAt())
            .build();
        when(taskService.updateTaskStatus(eq(1L), eq(TaskStatus.COMPLETED))).thenReturn(updated);

        mockMvc.perform(patch("/api/tasks/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} deletes a task")
    void deleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L);
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns 404 when not found")
    void getTaskNotFound() throws Exception {
        when(taskService.getTask(99L)).thenThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
            .andExpect(status().isNotFound());
    }
}
