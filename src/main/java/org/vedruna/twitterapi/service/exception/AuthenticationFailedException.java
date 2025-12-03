package org.vedruna.twitterapi.service.exception;

/**
 * Excepción de dominio que indica que la autenticación de un usuario ha fallado,
 * generalmente debido a credenciales incorrectas (usuario o contraseña).
 *
 * <p>Semántica:
 * <ul>
 *   <li>Se lanza durante el proceso de login si las credenciales proporcionadas
 *       no coinciden con las almacenadas.</li>
 *   <li>En la capa web, se recomienda mapear esta excepción a un
 *       <code>401 Unauthorized</code> para que el cliente reciba un error
 *       de autenticación.</li>
 * </ul>
 *
 * <p>Ejemplos de uso:
 * <ul>
 *   <li>Intentar iniciar sesión con un nombre de usuario existente pero contraseña incorrecta.</li>
 *   <li>Intentar usar un token JWT inválido o expirado (dependiendo de la lógica de autenticación).</li>
 * </ul>
 *
 * <p>Consideraciones de diseño:
 * <ul>
 *   <li>Es unchecked (hereda de {@link RuntimeException}) para que se propague
 *       hasta el manejador global sin forzar capturas intermedias.</li>
 *   <li>Si se desea, la respuesta al cliente puede incluir información adicional
 *       (por ejemplo, código interno o mensaje de error detallado) mediante
 *       {@code @ControllerAdvice}.</li>
 * </ul>
 *
 * <p>Hilos y seguridad: la clase no almacena estado mutable, por lo que es segura
 * para lanzarla desde múltiples hilos.</p>
 */
public class AuthenticationFailedException extends RuntimeException {

    /**
     * Constructor por defecto con mensaje genérico en inglés.
     */
    public AuthenticationFailedException() {
        super("Authentication failed: invalid credentials");
    }

    /**
     * Constructor que acepta un mensaje personalizado.
     *
     * @param message mensaje descriptivo del fallo de autenticación
     */
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
