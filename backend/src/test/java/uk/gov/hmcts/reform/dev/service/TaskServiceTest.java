package uk.gov.hmcts.reform.dev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.models.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.models.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
            .id(1L)
            .title("Review documents")
            .description("Optional notes")
            .status(TaskStatus.PENDING)
            .dueDateTime(LocalDateTime.now().plusDays(2))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should create and return a task")
    void createTask() {
        CreateTaskRequest request = new CreateTaskRequest(
            "Review documents",
            "Optional notes",
            TaskStatus.PENDING,
            LocalDateTime.now().plusDays(2)
        );
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse response = taskService.createTask(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Review documents");
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Review documents");
    }

    @Test
    @DisplayName("Should return task when found by id")
    void getTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse response = taskService.getTask(1L);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    @DisplayName("Should throw when task not found")
    void getTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask(99L))
            .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        assertThat(taskService.getAllTasks()).hasSize(1);
    }

    @Test
    @DisplayName("Should update task status")
    void updateTaskStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);

        assertThat(response.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should delete task")
    void deleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(sampleTask);
    }
}
