package org.vedruna.twitterapi.service;

import org.vedruna.twitterapi.controller.dto.LoginDto;
import org.vedruna.twitterapi.controller.dto.TokenDto;

/**
 * Servicio de autenticación temporal (solo para pruebas).
 */
public interface AuthService {
    /**
     * Intenta autenticar y devuelve un TokenDto si tiene éxito.
     */
    TokenDto login(LoginDto loginDto);
}
