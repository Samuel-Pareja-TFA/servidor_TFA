package org.vedruna.twitterapi.security.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.twitterapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.twitterapi.security.controller.dto.UserDTO;

/**
 * Componente que se encarga de convertir entre {@link UserEntity} y los DTOs
 * utilizados en la capa de seguridad.
 *
 * <p>Separa la lógica de conversión de la lógica de negocio, permitiendo
 * transformar entidades a DTOs para respuestas y viceversa para peticiones
 * de login o registro.</p>
 *
 * <p>DTOs soportados:
 * <ul>
 *   <li>{@link UserDTO}: DTO público de usuario</li>
 *   <li>{@link LoginRequestDTO}: DTO para login</li>
 *   <li>{@link RegisterRequestDTO}: DTO para registro de usuario</li>
 * </ul>
 * </p>
 */
@Component
public class UserConverter {

    /**
     * Convierte una entidad {@link UserEntity} a un DTO {@link UserDTO}.
     *
     * <p>Se utiliza para devolver información pública del usuario, evitando exponer
     * la contraseña u otros campos sensibles.</p>
     *
     * @param user entidad de usuario a convertir
     * @return DTO público {@link UserDTO} con id, username, email, descripción y fecha de creación
     */
    public UserDTO toDto(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setCreateDate(user.getCreateDate());
        return dto;
    }

    /**
     * Convierte un {@link LoginRequestDTO} a una {@link UserEntity} temporal.
     *
     * <p>Se utiliza durante la autenticación para extraer los datos enviados
     * en el login y pasarlos a la capa de servicio.</p>
     *
     * <p>Solo se establecen username y password.</p>
     *
     * @param request DTO con los datos de login
     * @return entidad {@link UserEntity} con username y password
     */
    public UserEntity loginToEntity(LoginRequestDTO request) {
        UserEntity u = new UserEntity();
        u.setUsername(request.getUsername());
        u.setPassword(request.getPassword());
        return u;
    }

    /**
     * Convierte un {@link RegisterRequestDTO} a una {@link UserEntity}.
     *
     * <p>Se utiliza durante el registro de un nuevo usuario para crear la
     * entidad que será persistida en la base de datos.</p>
     *
     * <p>Se establecen username, password, email y descripción.</p>
     *
     * @param request DTO con los datos de registro
     * @return entidad {@link UserEntity} lista para persistir
     */
    public UserEntity registerToEntity(RegisterRequestDTO request) {
        UserEntity u = new UserEntity();
        u.setUsername(request.getUsername());
        u.setPassword(request.getPassword());
        u.setEmail(request.getEmail());
        u.setDescription(request.getDescription());
        return u;
    }
}
