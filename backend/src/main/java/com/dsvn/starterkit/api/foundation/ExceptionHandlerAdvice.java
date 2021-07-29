package com.dsvn.starterkit.api.foundation;

import static com.dsvn.starterkit.domains.constant.ErrorCode.*;

import com.dsvn.starterkit.api.responses.ErrorPayload;
import com.dsvn.starterkit.api.responses.Response;
import com.dsvn.starterkit.domains.constant.ErrorCode;
import com.dsvn.starterkit.exceptions.PasswordNotMatchesException;
import com.dsvn.starterkit.exceptions.ResourceAlreadyExistException;
import com.dsvn.starterkit.infrastructure.security.PermissionCheck;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerAdvice {
    @Autowired private PermissionCheck permissionCheck;

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return Response.ofErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED, "method not allowed", METHOD_NOT_ALLOWED.getError());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> authenticationExceptionFailed(Exception e) {
        HttpStatus errorStatus =
                permissionCheck.isAuthenticated() ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;

        ErrorCode errorCode =
                permissionCheck.isAuthenticated() ? FORBIDDEN_ERROR : UNAUTHORIZED_ERROR;

        return Response.ofErrorResponse(errorStatus, e.getLocalizedMessage(), errorCode.getError());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> requestHandlingNoHandlerFound(NoHandlerFoundException ex) {
        return Response.ofErrorResponse(
                HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), API_NOT_FOUND.getError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationFail(MethodArgumentNotValidException e) {
        List<ErrorPayload.Error> errors = parseObjectError(e.getBindingResult().getAllErrors());

        return Response.ofErrorResponse(
                HttpStatus.BAD_REQUEST, errors, "validation error", VALIDATION_ERROR.getError());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> bindException(BindException e) {
        List<ErrorPayload.Error> errors = parseObjectError(e.getBindingResult().getAllErrors());

        return Response.ofErrorResponse(
                HttpStatus.BAD_REQUEST, errors, "validation error", VALIDATION_ERROR.getError());
    }

    @ExceptionHandler(PasswordNotMatchesException.class)
    public ResponseEntity<?> passwordNotMatchesHandle(PasswordNotMatchesException e) {
        return Response.ofErrorResponse(
                HttpStatus.BAD_REQUEST, e.getMessage(), PASSWORD_NOT_MATCHES_ERROR.getError());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> usernameNotFoundHandle(UsernameNotFoundException e) {
        return Response.ofErrorResponse(
                HttpStatus.BAD_REQUEST, e.getMessage(), USER_NOT_FOUND_ERROR.getError());
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<?> resourceAlreadyExistException(ResourceAlreadyExistException e) {
        return Response.ofErrorResponse(
                HttpStatus.BAD_REQUEST, e.getMessage(), RESOURCE_ALREADY_EXIST.getError());
    }

    @ExceptionHandler
    public ResponseEntity<?> internalServerError(Exception e) {
        log.error(String.format("Uncaught Exception : %s : %s ", e.getClass(), e.getMessage()), e);
        return Response.ofErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "internal server error",
                INTERNAL_ERROR.getError());
    }

    private List<ErrorPayload.Error> parseObjectError(List<ObjectError> objectErrors) {
        List<ErrorPayload.Error> errors = new ArrayList<>();

        for (ObjectError objectError : objectErrors) {
            Object[] objects = objectError.getArguments();
            if (objects != null && objects.length > 0) {
                DefaultMessageSourceResolvable messageSourceResolvable =
                        (DefaultMessageSourceResolvable) objects[0];
                String code = messageSourceResolvable.getDefaultMessage();
                ErrorPayload.Error payloadError =
                        new ErrorPayload.Error(objectError.getDefaultMessage(), code);
                errors.add(payloadError);
            }
        }

        return errors;
    }
}
