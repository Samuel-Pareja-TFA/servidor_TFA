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
 * Servicio de autenticación: login, register y refresh token.
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

    public UserEntity register(UserEntity user) {
        RoleEntity rol = roleRepo.findByName("Usuario")
                .orElseThrow(() -> new NoSuchElementException("Role not found"));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateDate(LocalDate.now());
        user.setRole(rol);
        return userRepo.save(user);
    }

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
