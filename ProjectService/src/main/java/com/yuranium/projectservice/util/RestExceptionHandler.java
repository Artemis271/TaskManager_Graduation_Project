package com.yuranium.projectservice.util;

import com.yuranium.projectservice.util.exception.AccessDeniedException;
import com.yuranium.projectservice.util.exception.ProjectEntityNotFoundException;
import com.yuranium.projectservice.util.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(ProjectEntityNotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleProjectEntityNotFoundException(ProjectEntityNotFoundException exc)
    {
        return new ResponseEntity<>(
                new NotFoundResponse(404, HttpStatus.NOT_FOUND,
                        LocalDateTime.now(), exc.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<NotFoundResponse> handleAccessDeniedException(AccessDeniedException exc)
    {
        return new ResponseEntity<>(
                new NotFoundResponse(403, HttpStatus.FORBIDDEN,
                        LocalDateTime.now(), exc.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }
}