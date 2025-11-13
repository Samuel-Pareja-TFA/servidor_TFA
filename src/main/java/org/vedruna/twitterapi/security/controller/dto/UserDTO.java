package org.vedruna.twitterapi.security.controller.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserDTO {
    Integer userId;
    String username;
    String email;
    String description;
    LocalDate createDate;
}
