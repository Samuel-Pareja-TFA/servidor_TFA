package org.vedruna.twitterapi.service.exception;

import java.util.NoSuchElementException;

/**
 * Excepción de dominio que indica que un {@code User} no existe en el sistema.
 *
 * <p>Esta excepción extiende {@link NoSuchElementException}, que a su vez es una
 * {@link RuntimeException}. Se diseñó así para facilitar el lanzamiento desde las
 * capas de servicio sin forzar la declaración en las firmas de los métodos. En la
 * capa de presentación (controladores) se recomienda capturar esta excepción y
 * mapearla a una respuesta HTTP 404 (Not Found).
 *
 * <p>Uso típico:
 * <pre>
 * userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
 * </pre>
 *
 * Consideraciones:
 * <ul>
 *   <li>No contiene información adicional más que el mensaje. Si se requiere más
 *       contexto (por ejemplo un código de error o campos JSON estructurados), puede
 *       extenderse la clase para incluirlos o usar un handler global que transforme
 *       la excepción en un payload HTTP enriquecido.</li>
 *   <li>Al ser una unchecked exception, su lanzamiento no obliga a los llamadores a
 *       capturarla, por lo que la responsabilidad de mapearla a una respuesta adecuada
 *       recae en la capa de entrada (controladores/handlers).</li>
 * </ul>
 */
public class UserNotFoundException extends NoSuchElementException {

    /**
     * Constructor por defecto con un mensaje estándar.
     *
     * <p>Útil para lanzamientos rápidos donde no se necesita un mensaje detallado.
     */
    public UserNotFoundException() {
        super("User not found");
    }

    /**
     * Constructor que permite especificar un mensaje personalizado.
     *
     * <p>Se recomienda usar mensajes que contengan suficiente contexto para depuración
     * (por ejemplo el id del recurso buscado), pero sin exponer información sensible.
     *
     * @param message mensaje descriptivo de la excepción.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
