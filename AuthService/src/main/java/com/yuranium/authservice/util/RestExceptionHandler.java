package com.yuranium.authservice.util;

import com.yuranium.authservice.util.exception.EntityNotFoundException;
import com.yuranium.authservice.util.exception.UserEntityAlreadyExistsException;
import com.yuranium.authservice.util.exception.UserEntityNotFoundException;
import com.yuranium.authservice.util.response.RestResponse;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler
{
    @ExceptionHandler(UserEntityNotFoundException.class)
    public ResponseEntity<RestResponse> handleEntityNotFoundException(EntityNotFoundException exc)
    {
        return new ResponseEntity<>(new RestResponse(
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND,
                LocalDateTime.now(), exc.getMessage()
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RestResponse> handleBadCredentialsException(BadCredentialsException exc)
    {
        return new ResponseEntity<>(new RestResponse(
                HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(), exc.getMessage()
        ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<RestResponse> handleDisabledException(DisabledException exc)
    {
        return new ResponseEntity<>(new RestResponse(
                HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(), exc.getMessage()
        ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserEntityAlreadyExistsException.class)
    public ResponseEntity<RestResponse> handleUserEntityAlreadyExistsException(
            UserEntityAlreadyExistsException exc)
    {
        return new ResponseEntity<>(new RestResponse(
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST,
                LocalDateTime.now(), exc.getMessage()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<RestResponse> handleMalformedJwtException(
            MalformedJwtException exc)
    {
        return new ResponseEntity<>(new RestResponse(
                HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(), exc.getMessage()
        ), HttpStatus.UNAUTHORIZED);
    }
}