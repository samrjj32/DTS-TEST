package uk.gov.hmcts.reform.dev.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for creating a new task")
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    @Schema(example = "Review case documents", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    @Schema(example = "Check all submitted evidence before hearing")
    private String description;

    @NotNull(message = "Status is required")
    @Schema(example = "PENDING", requiredMode = Schema.RequiredMode.REQUIRED)
    private TaskStatus status;

    @NotNull(message = "Due date/time is required")
    @Future(message = "Due date/time must be in the future")
    @Schema(example = "2026-06-01T14:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dueDateTime;
}
