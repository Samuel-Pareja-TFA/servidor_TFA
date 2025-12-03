package org.vedruna.twitterapi.service.exception;

/**
 * Excepción de dominio que indica un conflicto de unicidad sobre el campo {@code username}.
 *
 * <p>Se lanza cuando se intenta crear o actualizar un usuario con un nombre de usuario que ya
 * existe en la base de datos. Por convención HTTP esta situación suele mapearse a un código
 * 409 (Conflict) en la capa de presentación.
 *
 * <p>Notas y recomendaciones:
 * <ul>
 *   <li>Es una excepción unchecked ({@link RuntimeException}) para no forzar su declaración en
 *       firmas. La capa de controladores debe capturarla y transformarla en una respuesta
 *       válida (por ejemplo, 409 + payload con mensaje y código interno opcional).</li>
 *   <li>Para internacionalización o respuestas API más ricas, considere envolver esta excepción
 *       en un objeto de error que incluya un código, mensaje localizable y datos adicionales.</li>
 *   <li>No incluir datos sensibles en el mensaje de la excepción; preferir identificar el campo
 *       conflictivo sin exponer información privada.
 * </ul>
 */
public class UsernameConflictException extends RuntimeException {

    /**
     * Constructor por defecto con mensaje estándar.
     */
    public UsernameConflictException() {
        super("Username already exists");
    }

    /**
     * Constructor que permite un mensaje personalizado.
     *
     * @param message mensaje descriptivo de la razón del conflicto.
     */
    public UsernameConflictException(String message) {
        super(message);
    }
}
