package org.vedruna.twitterapi.controller.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Anotación de validación personalizada para verificar la existencia de un usuario
 * en la base de datos.
 *
 * <p>Se utiliza sobre campos o parámetros de tipo {@link Integer} que representen
 * un {@code userId}. Esta anotación delega la validación a la clase
 * {@link ExistingUserValidator}.</p>
 *
 * <p>Uso típico:
 * <pre>
 * {@code
 * @ExistingUser
 * private Integer userId;
 * }
 * </pre>
 * </p>
 *
 * <p>Parámetros disponibles:
 * <ul>
 *   <li>{@link #message()} mensaje de error si el usuario no existe (por defecto "User does not exist").</li>
 *   <li>{@link #groups()} permite agrupar restricciones para validaciones específicas.</li>
 *   <li>{@link #payload()} permite adjuntar metadatos adicionales al error de validación.</li>
 * </ul>
 * </p>
 *
 * <p>Consideraciones:
 * <ul>
 *   <li>Se puede aplicar a campos ({@code FIELD}) o parámetros de método ({@code PARAMETER}).</li>
 *   <li>La validación se realiza automáticamente cuando se usa junto con
 *       {@link jakarta.validation.Valid} en DTOs o parámetros de controlador.</li>
 * </ul>
 * </p>
 */
@Constraint(validatedBy = org.vedruna.twitterapi.controller.validation.ExistingUserValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingUser {

    /**
     * Mensaje de error que se devuelve si la validación falla.
     *
     * @return mensaje de error
     */
    String message() default "User does not exist";

    /**
     * Grupos de validación a los que pertenece esta restricción.
     *
     * @return array de clases de grupo
     */
    Class<?>[] groups() default {};

    /**
     * Payload que puede ser utilizado para transportar metadatos adicionales
     * sobre la violación de la restricción.
     *
     * @return array de clases que extienden Payload
     */
    Class<? extends Payload>[] payload() default {};
}
