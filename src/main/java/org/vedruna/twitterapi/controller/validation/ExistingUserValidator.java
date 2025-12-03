package org.vedruna.twitterapi.controller.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vedruna.twitterapi.persistance.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador de la anotación {@link ExistingUser}.
 *
 * <p>Su función principal es verificar que un {@code userId} proporcionado como
 * valor de un campo o parámetro exista en la base de datos. Esto es útil para
 * asegurar que las operaciones que dependen de la existencia de un usuario
 * válido no fallen por referencias nulas o inválidas.</p>
 *
 * <p>Comportamiento:
 * <ul>
 *   <li>Si el valor es {@code null}, retorna {@code false} (inválido).</li>
 *   <li>Si el valor existe en la base de datos (consultando {@link UserRepository}),
 *       retorna {@code true} (válido).</li>
 *   <li>Si no existe, retorna {@code false} y el mensaje de validación
 *       definido en la anotación {@link ExistingUser} será mostrado.</li>
 * </ul>
 * </p>
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>Inyecta {@link UserRepository} mediante constructor para acceder a la base de datos.</li>
 *   <li>Implementa {@link ConstraintValidator} parametrizado con {@link ExistingUser} y {@link Integer}.</li>
 *   <li>Es seguro para ser usado en entornos concurrentes ya que no mantiene estado mutable.</li>
 * </ul>
 * </p>
 */
@Component
public class ExistingUserValidator implements ConstraintValidator<org.vedruna.twitterapi.controller.validation.ExistingUser, Integer> {

    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencia de {@link UserRepository}.
     *
     * @param userRepository repositorio para consultar la existencia del usuario
     */
    @Autowired
    public ExistingUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Inicializa el validador. Se deja vacío porque no se requiere configuración
     * adicional en este caso.
     *
     * @param constraintAnnotation anotación que activa este validador
     */
    @Override
    public void initialize(org.vedruna.twitterapi.controller.validation.ExistingUser constraintAnnotation) { }

    /**
     * Valida que el valor del {@code userId} exista en la base de datos.
     *
     * @param value valor a validar (userId)
     * @param context contexto del validador
     * @return {@code true} si el {@code userId} existe; {@code false} si es {@code null} o no existe
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return userRepository.existsById(value);
    }
}
