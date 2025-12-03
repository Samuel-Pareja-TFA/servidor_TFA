package org.vedruna.twitterapi.service;

import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;

/**
 * Contrato del servicio de autenticación.
 *
 * <p>Esta interfaz define las operaciones mínimas necesarias para autenticar
 * usuarios y devolver tokens que representen la sesión/autorización. En este
 * proyecto la implementación actual se utiliza de forma temporal para pruebas,
 * pero la interfaz debe expresar claramente responsabilidades y expectativas
 * para una implementación productiva (validación, manejo de errores,
 * expiración de tokens, etc.).</p>
 *
 * <p>Consideraciones y responsabilidades de la implementación:</p>
 * <ul>
 *   <li>Validar las credenciales recibidas en {@link LoginDto} y devolver un
 *       {@link TokenDto} con el token de acceso (y opcionalmente refresh
 *       token) en caso de éxito.</li>
 *   <li>En caso de credenciales inválidas, la implementación debe lanzar una
 *       excepción adecuada (por ejemplo una excepción custom o
 *       {@code AuthenticationException}) que la capa de controlador mapeará
 *       a un código HTTP 401/403 según proceda.</li>
 *   <li>La implementación debe encargarse de la política de expiración de
 *       tokens, firmas (claves secretas) y de cualquier almacenamiento
 *       asociado (p. ej. lista de refresh tokens revocados).</li>
 * </ul>
 */
public interface AuthService {

    /**
     * Intenta autenticar al usuario con las credenciales contenidas en
     * {@code loginDto} y, si la autenticación es satisfactoria, devuelve un
     * {@link TokenDto} que contiene el token de acceso (y opcionalmente un
     * refresh token u otra metadata).
     *
     * <p>Entrada: {@link LoginDto} que típicamente contiene username/email y
     * password (en texto plano en la llamada; la implementación debe validar
     * y comparar el hash). La implementación debe:<ul>
     *   <li>Validar que los campos obligatorios están presentes.</li>
     *   <li>Comprobar las credenciales contra el almacén de usuarios.</li>
     *   <li>Generar y devolver un {@link TokenDto} con la información del
     *       token en caso de éxito.</li>
     * </ul></p>
     *
     * @param loginDto DTO con credenciales de autenticación.
     * @return {@link TokenDto} con el token de acceso y metadatos.
     * @throws org.springframework.security.core.AuthenticationException
     *         o una excepción custom si las credenciales son inválidas o hay
     *         otro problema durante la autenticación.
     */
    TokenDto login(LoginDto loginDto);
}
