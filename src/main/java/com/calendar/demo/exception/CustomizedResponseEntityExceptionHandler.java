package com.calendar.demo.exception;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AppException.class)
    public final ResponseEntity<Map> handleAppException(final AppException ex, final WebRequest request) {
        log.error("Handled Error - AppException - {}", ex.toString());
        return new ResponseEntity<>(ex.getParamsMap(), ex.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        return getBindingErrorResponse(ex, ex.getBindingResult());
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return getBindingErrorResponse(ex, ex.getBindingResult());
    }

    @NotNull
    private ResponseEntity<Object> getBindingErrorResponse(Exception ex, BindingResult result) {
        List<FieldError> fieldErrors = result.getFieldErrors();
        Map<String, String> errors = fieldErrors.stream().collect(
                Collectors.toMap(FieldError::getField, f -> Optional.ofNullable(f.getDefaultMessage()).orElse("")));
        final AppException exception = AppException.builder().statusCode(HttpStatus.BAD_REQUEST).errorCode("bad_request")
                .message("Something went wrong. Please try again in sometime.")
                .information(ImmutableMap.of("errors", errors)).build();
        log.error("Handled Error - AppException - {}", ex.toString());
        return new ResponseEntity<>(exception.getParamsMap(), exception.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers,
                                                                          HttpStatusCode status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        final AppException exception = AppException.builder().statusCode(HttpStatus.BAD_REQUEST).errorCode("bad_request").message(error).build();
        return new ResponseEntity<>(exception.getParamsMap(), exception.getStatusCode());
    }

    @Override
    public ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status,
                                                                       WebRequest request) {
        String error = ex.getMessage();
        final AppException exception = AppException.builder().statusCode(HttpStatus.BAD_REQUEST).errorCode("bad_request").message(error).build();
        return new ResponseEntity<>(exception.getParamsMap(), exception.getStatusCode());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        final String[] split = ex.getCause().getMessage().split("\n");
        String error = split.length > 0 ? split[0] : ex.getCause().getMessage();
        final AppException exception = AppException.builder().statusCode(HttpStatus.BAD_REQUEST).errorCode("bad_request").message(error).build();
        return new ResponseEntity<>(exception.getParamsMap(), exception.getStatusCode());
    }

    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        final String className = Objects.nonNull(ex.getRequiredType()) ? ex.getRequiredType().getName() : "not known";
        String error = ex.getName() + " should be of type " + className;
        final AppException appException = AppException.builder().statusCode(HttpStatus.BAD_REQUEST).errorCode("bad_request").message(error).build();
        return handleAppException(appException, request);
    }

    @ExceptionHandler (Exception.class)
    public final ResponseEntity<Map> handleDefaultException(final Exception ex, final WebRequest request) {
        log.error("Unknown Exception handled - ", ex);
        final AppException appException = AppException.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR).errorCode("unknown_error")
                .message("Something went wrong. Please try again.").build();
        return handleAppException(appException, request);
    }
}
