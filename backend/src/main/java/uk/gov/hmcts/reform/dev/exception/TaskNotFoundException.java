package uk.gov.hmcts.reform.dev.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(final Long id) {
        super("Task not found with id: " + id);
    }
}
