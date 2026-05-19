package uk.gov.hmcts.reform.dev.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.dev.models.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for updating a task's status")
public class UpdateTaskStatusRequest {

    @NotNull(message = "Status is required")
    @Schema(example = "IN_PROGRESS", requiredMode = Schema.RequiredMode.REQUIRED)
    private TaskStatus status;
}
