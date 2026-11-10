package com.example.dealership.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMethodArgumentNotValidReturnsObjectLevelErrorsWithNullFieldBeforeFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.reject("invalid", "is not consistent");
        bindingResult.addError(new FieldError("request", "name", "must not be blank"));

        ProblemDetail problem = handler.handleMethodArgumentNotValid(
                new MethodArgumentNotValidException(null, bindingResult));

        assertThat(problem.getStatus()).isEqualTo(400);
        assertThat(problem.getDetail()).isEqualTo("Validation failed");
        assertThat(problem.getProperties()).containsEntry("errors", List.of(
                new ValidationError(null, "is not consistent"),
                new ValidationError("name", "must not be blank")));
    }
}
