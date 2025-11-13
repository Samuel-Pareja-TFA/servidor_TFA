package org.vedruna.twitterapi.security.controller.converter;

import org.springframework.stereotype.Component;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.security.controller.dto.LoginRequestDTO;
import org.vedruna.twitterapi.security.controller.dto.RegisterRequestDTO;
import org.vedruna.twitterapi.security.controller.dto.UserDTO;

/**
 * Conversor entre UserEntity y DTOs de seguridad.
 */
@Component
public class UserConverter {

    public UserDTO toDto(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setCreateDate(user.getCreateDate());
        return dto;
    }

    public UserEntity loginToEntity(LoginRequestDTO request) {
        UserEntity u = new UserEntity();
        u.setUsername(request.getUsername());
        u.setPassword(request.getPassword());
        return u;
    }

    public UserEntity registerToEntity(RegisterRequestDTO request) {
        UserEntity u = new UserEntity();
        u.setUsername(request.getUsername());
        u.setPassword(request.getPassword());
        u.setEmail(request.getEmail());
        u.setDescription(request.getDescription());
        return u;
    }
}
