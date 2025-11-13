package org.vedruna.twitterapi.controller.handler;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import jakarta.validation.ValidationException;

import lombok.extern.slf4j.Slf4j;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;
import org.vedruna.twitterapi.service.exception.PublicationNotFoundException;
import org.vedruna.twitterapi.service.exception.AuthenticationFailedException;

/**
 * Controlador global de manejo de excepciones para la API.
 *
 * <p>Este {@link RestControllerAdvice} captura excepciones lanzadas en cualquier
 * controlador REST y convierte los errores en respuestas HTTP con {@link ProblemDetail},
 * incluyendo un código de estado adecuado, título, detalle y tipo (URI de referencia).</p>
 *
 * <p>Incluye manejo de:
 * <ul>
 *   <li>Excepciones de validación y desajuste de tipo</li>
 *   <li>Excepciones de base de datos (integridad y resultados vacíos)</li>
 *   <li>Excepciones personalizadas de negocio (usuario o publicación no encontrada, login fallido)</li>
 *   <li>Excepciones de transacción (RollbackException)</li>
 *   <li>Otros errores HTTP (método no soportado, cuerpo mal formado, propiedades inválidas)</li>
 * </ul>
 * </p>
 *
 * <p>Excepciones personalizadas:
 * <ul>
 *   <li>{@link UserNotFoundException}: usuario no encontrado (404)</li>
 *   <li>{@link PublicationNotFoundException}: publicación no encontrada (404)</li>
 *   <li>{@link AuthenticationFailedException}: login fallido (401)</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class HandlerExceptionController extends ResponseEntityExceptionHandler {

     /**
     * Maneja errores cuando un parámetro no coincide con el tipo esperado.
     * Ej: ?id=abc cuando se esperaba un Integer.
     *
     * @param ex la excepción lanzada
     * @param request el contexto de la petición
     * @return {@link ResponseEntity} con status 400 y detalle del error
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            String.format("The parameter '%s' with value '%s' must be of type '%s'.",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "a different type"
            )
        );
        problemDetail.setTitle("MethodArgumentTypeMismatchException: Invalid Argument Type");
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/method/annotation/MethodArgumentTypeMismatchException.html"));
        log.warn("Mismatched Argument Type (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

     /**
     * Maneja errores de validación inesperados (tipo de campo incorrecto en anotaciones de validación).
     */
    @ExceptionHandler(UnexpectedTypeException.class)
    protected ResponseEntity<Object> handleUnexpectedType(UnexpectedTypeException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "A type mismatch was encountered during validation processing. Check if validation annotations are applied correctly to the target field's type."
        );
        problemDetail.setTitle("UnexpectedTypeException: Unexpected Validation Type");
        problemDetail.setType(URI.create("https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/UnexpectedTypeException.html"));
        log.warn("Unexpected Type Exception during validation (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja {@link IllegalArgumentException} lanzadas por lógica de negocio inválida.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null ? ex.getMessage() : "Invalid business argument."
        );
        problemDetail.setTitle("IllegalArgumentException: Invalid Business Argument");
        problemDetail.setType(URI.create("https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/IllegalArgumentException.html"));
        log.warn("Illegal Argument Exception (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    
    /**
     * Maneja {@link PublicationNotFoundException} cuando una publicación no existe.
     */
    @ExceptionHandler(PublicationNotFoundException.class)
    protected ResponseEntity<Object> handlePublicationNotFound(PublicationNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage() != null ? ex.getMessage() : "The requested publication was not found."
        );
        problemDetail.setTitle("PublicationNotFoundException: Publication Not Found");
        log.warn("Publication Not Found (404): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Maneja {@link UserNotFoundException} cuando un usuario no existe.
     */
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            ex.getMessage() != null ? ex.getMessage() : "The requested user was not found."
        );
        problemDetail.setTitle("UserNotFoundException: User Not Found");
        log.warn("User Not Found (404): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Maneja la excepción personalizada {@code AuthenticationFailedException}
     * cuando las credenciales no son válidas en el login.
     * Genera una respuesta 401 Unauthorized con ProblemDetail.
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    protected ResponseEntity<Object> handleAuthenticationFailed(AuthenticationFailedException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage() != null ? ex.getMessage() : "Authentication failed: invalid credentials"
        );
        problemDetail.setTitle("AuthenticationFailedException: Invalid credentials");
        // Tipo apuntando a docu (puedes cambiar a una URL de tu API si quieres)
        problemDetail.setType(URI.create("https://datatracker.ietf.org/doc/html/rfc7235#section-3.1"));
        // Puedes añadir propiedades adicionales si te interesa (ej. reason, code)
        problemDetail.setProperty("reason", "invalid_credentials");
        log.warn("Authentication failed (401): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

     /**
     * Maneja {@link EmptyResultDataAccessException} cuando la entidad no existe en la BD.
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> handleEmptyResultDataAccess(EmptyResultDataAccessException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            "The requested resource was not found in the database. It may have been deleted or the identifier is incorrect."
        );
        problemDetail.setTitle("EmptyResultDataAccessException: Resource Not Found");
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/EmptyResultDataAccessException.html"));
        log.warn("Empty Result Data Access Exception (404): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

     /**
     * Maneja {@link DataIntegrityViolationException} para conflictos de integridad de datos (unique o FK).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The request could not be completed due to a conflict with existing data or a data constraint (unique constraint or foreign-key)."
        );
        problemDetail.setTitle("DataIntegrityViolationException: Data Conflict");
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/DataIntegrityViolationException.html"));
        log.warn("Data Integrity Violation (409 Conflict): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Maneja {@link HttpMessageNotReadableException} cuando el cuerpo de la petición es inválido o mal formado.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getRootCause() != null ? ex.getRootCause().getLocalizedMessage() : "The request body could not be read or is malformed. Please check JSON syntax and data types."
        );
        problemDetail.setTitle("HttpMessageNotReadableException: Malformed Body");
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/converter/HttpMessageNotReadableException.html"));
        log.warn("HTTP Message Not Readable (Malformed Body): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
    * Maneja {@link ConstraintViolationException} lanzadas por violaciones de restricciones de validación.
    */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing
            ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "One or more constraints were violated. Check 'errors' property for details."
        );
        problemDetail.setTitle("ConstraintViolationException: Constraint Violation");
        problemDetail.setProperty("errors", errors);
        problemDetail.setType(URI.create("https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/ConstraintViolationException.html"));
        log.warn("Constraint Violation Exception (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja {@link MethodArgumentNotValidException} lanzadas cuando la validación de un objeto falla.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> (error.getDefaultMessage() != null) ? error.getDefaultMessage() : "Invalid value",
                (existing, replacement) -> existing
            ));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "The request body contains invalid data. Check 'errors' property for field details."
        );
        problemDetail.setTitle("MethodArgumentNotValidException: Validation Error");
        problemDetail.setProperty("errors", errors);
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/MethodArgumentNotValidException.html"));
        log.warn("Method Argument Not Valid Exception (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja {@link ValidationException} genéricas de validación.
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<Object> handleValidation(ValidationException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null && !ex.getMessage().trim().isEmpty() ? ex.getMessage() : "The request data is invalid."
        );
        problemDetail.setTitle("ValidationException: Validation Failed");
        problemDetail.setType(URI.create("https://jakarta.ee/specifications/bean-validation/3.0/apidocs/jakarta/validation/ValidationException.html"));
        log.warn("Validation Exception (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Maneja {@link RollbackException} lanzadas cuando una transacción falla y se revierte.
     */
    @ExceptionHandler(RollbackException.class)
    protected ResponseEntity<Object> handleRollBack(RollbackException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred while processing the transaction. Operation could not be completed."
        );
        problemDetail.setTitle("RollbackException: Internal Server Error");
        problemDetail.setType(URI.create("https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/rollbackexception"));
        log.error("Transaction Rollback Failure (500): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Maneja {@link HttpRequestMethodNotSupportedException} cuando el método HTTP no es soportado por el endpoint.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.METHOD_NOT_ALLOWED,
            ex.getMessage() != null && !ex.getMessage().trim().isEmpty() ? ex.getMessage() : "HTTP method not supported for this resource."
        );
        problemDetail.setTitle("HttpRequestMethodNotSupportedException: HTTP Method Not Allowed");
        problemDetail.setType(URI.create("https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/HttpRequestMethodNotSupportedException.html"));

        if (ex.getSupportedHttpMethods() != null && !ex.getSupportedHttpMethods().isEmpty()) {
            headers.setAllow(ex.getSupportedHttpMethods());
            Set<String> allowedMethodsStrings = ex.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .collect(Collectors.toSet());
            problemDetail.setProperty("allowedMethods", allowedMethodsStrings);
        }

        log.warn("HTTP Method Not Supported (405): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    /**
     * Maneja {@link PropertyReferenceException} cuando una propiedad inválida es usada en sorting o searching.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    protected ResponseEntity<Object> handlePropertyReference(PropertyReferenceException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage() != null && !ex.getMessage().trim().isEmpty() ? ex.getMessage() : "Invalid property for sorting or searching."
        );
        problemDetail.setTitle("PropertyReferenceException: Invalid Property Reference");
        problemDetail.setProperty("invalidProperty", ex.getPropertyName());
        problemDetail.setType(URI.create("https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/mapping/PropertyReferenceException.html"));
        log.warn("Property Reference Exception (400): {}", ex.toString());
        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    
    
}