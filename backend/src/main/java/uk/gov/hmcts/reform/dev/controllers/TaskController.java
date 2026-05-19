package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.models.dto.CreateTaskRequest;
import uk.gov.hmcts.reform.dev.models.dto.TaskResponse;
import uk.gov.hmcts.reform.dev.models.dto.UpdateTaskStatusRequest;
import uk.gov.hmcts.reform.dev.service.TaskService;

import java.util.List;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Caseworker task management API")
public class TaskController {

    private final TaskService taskService;

    @PostMapping(produces = "application/json", consumes = "application/json")
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody final CreateTaskRequest request) {
        return status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Retrieve a task by ID")
    public ResponseEntity<TaskResponse> getTask(@PathVariable final Long id) {
        return ok(taskService.getTask(id));
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Retrieve all tasks")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ok(taskService.getAllTasks());
    }

    @PatchMapping(value = "/{id}/status", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Update the status of a task")
    public ResponseEntity<TaskResponse> updateTaskStatus(
        @PathVariable final Long id,
        @Valid @RequestBody final UpdateTaskStatusRequest request
    ) {
        return ok(taskService.updateTaskStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(@PathVariable final Long id) {
        taskService.deleteTask(id);
        return noContent().build();
    }
}
