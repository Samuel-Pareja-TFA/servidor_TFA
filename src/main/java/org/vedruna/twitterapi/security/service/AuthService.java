package org.vedruna.twitterapi.security.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.vedruna.twitterapi.persistance.entity.RoleEntity;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.RoleRepository;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.security.controller.dto.AuthResponseDTO;

import lombok.AllArgsConstructor;

/**
 * Servicio de autenticación que gestiona:
 * <ul>
 *   <li>Login de usuarios mediante username y password.</li>
 *   <li>Registro de nuevos usuarios con rol por defecto.</li>
 *   <li>Renovación de Access Token usando Refresh Token.</li>
 * </ul>
 *
 * <p>Se integra con Spring Security para autenticar usuarios y con {@link JWTServiceImpl}
 * para generar y validar tokens JWT.</p>
 *
 * <p>Depende de {@link UserRepository} y {@link RoleRepository} para acceder a la base de datos,
 * {@link PasswordEncoder} para codificar contraseñas, y {@link UserDetailsService} para cargar usuarios
 * a partir del username.</p>
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final JWTServiceImpl jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Autentica un usuario y genera tokens JWT.
     *
     * <p>El método realiza los siguientes pasos:</p>
     * <ol>
     *   <li>Autentica las credenciales usando {@link AuthenticationManager}.</li>
     *   <li>Carga el usuario completo desde la base de datos.</li>
     *   <li>Genera un access token y un refresh token usando {@link JWTServiceImpl}.</li>
     *   <li>Devuelve un {@link AuthResponseDTO} con los tokens y el tiempo de expiración.</li>
     * </ol>
     *
     * @param user usuario con username y password a autenticar
     * @return DTO con access token, refresh token y expiración
     * @throws NoSuchElementException si el usuario no existe en la base de datos
     */
    public AuthResponseDTO login(UserEntity user) {
        // autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        // cargar usuario completo
        UserEntity userEntity = userRepo.findByUsername(user.getUsername());
        if (userEntity == null) {
            throw new NoSuchElementException("User not found");
        }

        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .expiresIn(jwtService.getAccessTokenExpiresIn())
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * <p>El método realiza los siguientes pasos:</p>
     * <ol>
     *   <li>Obtiene el rol por defecto "Usuario" desde {@link RoleRepository}.</li>
     *   <li>Codifica la contraseña usando {@link PasswordEncoder}.</li>
     *   <li>Asigna la fecha de creación actual y el rol al usuario.</li>
     *   <li>Guarda el usuario en la base de datos usando {@link UserRepository}.</li>
     * </ol>
     *
     * @param user entidad de usuario a registrar
     * @return usuario registrado con ID y rol asignado
     * @throws NoSuchElementException si no se encuentra el rol por defecto
     */
    public UserEntity register(UserEntity user) {
        RoleEntity rol = roleRepo.findByName("Usuario")
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateDate(LocalDate.now());
        user.setRole(rol);
        return userRepo.save(user);
    }

    /**
     * Renueva el access token a partir de un refresh token válido.
     *
     * <p>El método realiza los siguientes pasos:</p>
     * <ol>
     *   <li>Obtiene el username contenido en el refresh token.</li>
     *   <li>Carga los detalles del usuario desde {@link UserDetailsService}.</li>
     *   <li>Valida que el refresh token sea válido y no esté expirado.</li>
     *   <li>Genera un nuevo access token.</li>
     *   <li>Devuelve un {@link AuthResponseDTO} con el nuevo access token y el refresh token original.</li>
     * </ol>
     *
     * @param refreshToken token de refresco recibido
     * @return DTO con nuevo access token, refresh token y expiración
     * @throws IllegalArgumentException si el refresh token no es válido o ha expirado
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        final String username = jwtService.getUsernameFromRefreshToken(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Refresh token invalid/expired");
        }

        UserEntity userEntity = (UserEntity) userDetails;
        String newAccess = jwtService.generateAccessToken(userEntity);

        return AuthResponseDTO.builder()
                .accessToken(newAccess)
                .expiresIn(jwtService.getAccessTokenExpiresIn())
                .refreshToken(refreshToken)
                .build();
    }
}
