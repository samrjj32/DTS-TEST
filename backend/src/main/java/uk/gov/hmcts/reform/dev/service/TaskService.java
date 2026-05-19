package uk.gov.hmcts.reform.dev.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.dev.exception.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.models.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.models.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskResponse createTask(final CreateTaskRequest request) {
        LocalDateTime now = LocalDateTime.now();
        Task task = Task.builder()
            .title(request.getTitle().trim())
            .description(request.getDescription() != null ? request.getDescription().trim() : null)
            .status(request.getStatus())
            .dueDateTime(request.getDueDateTime())
            .createdAt(now)
            .updatedAt(now)
            .build();
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(final Long id) {
        return TaskResponse.fromEntity(findTaskOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(TaskResponse::fromEntity)
            .toList();
    }

    @Transactional
    public TaskResponse updateTaskStatus(final Long id, final TaskStatus status) {
        Task task = findTaskOrThrow(id);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(final Long id) {
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
    }

    private Task findTaskOrThrow(final Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
