package org.vedruna.twitterapi.security.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.vedruna.twitterapi.persistance.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio para creación y validación de JWT (Access + Refresh).
 * Implementación escrita para ser compatible con distintos entornos
 * (usa Jwts.parser() como fallback para evitar problemas de versiones).
 */
@Service
public class JWTServiceImpl {

    @Value("${auth.access-token-secret-key}")
    private String accessTokenSecretKey;

    @Value("${auth.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${auth.refresh-token-secret-key}")
    private String refreshTokenSecretKey;

    @Value("${auth.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private SecretKey getKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails user, Long expirationTime, String secretKey) {
        // nota: setClaims/setSubject/setIssuedAt/setExpiration aparecen como deprecated en algunas versiones,
        // pero siguen funcionando. Si tu versión soporta builder/claim settings más modernos puedes adaptarlo.
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getKey(secretKey))
                .compact();
    }

    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        return buildToken(claims, user, accessTokenExpiration, accessTokenSecretKey);
    }

    public String generateRefreshToken(UserEntity user) {
        return buildToken(new HashMap<>(), user, refreshTokenExpiration, refreshTokenSecretKey);
    }

    /**
     * Intenta obtener Claims; usa parser() (fallback). parserBuilder() idealmente estaría disponible,
     * pero parser() es más compatible con instalaciones antiguas y evita problemas cuando Maven
     * carga otra implementación.
     */
    private Claims getAllClaims(String token, String secretKey) {
    // Llamamos build() en el builder para obtener el JwtParser y luego parseamos.
    return Jwts.parser()
            .setSigningKey(getKey(secretKey))
            .build()                       // <- IMPORTANTE: construir el parser aquí
            .parseClaimsJws(token)
            .getBody();
    }


    public <T> T getClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = getAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromAccessToken(String token) {
        return getClaim(token, Claims::getSubject, accessTokenSecretKey);
    }

    public Long getAccessTokenExpiresIn() {
        return accessTokenExpiration / 1000;
    }

    private Date getAccessTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, accessTokenSecretKey);
    }

    private boolean isAccessTokenExpired(String token) {
        return getAccessTokenExpiration(token).before(new Date());
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromAccessToken(token);
        return (username.equals(userDetails.getUsername()) && !isAccessTokenExpired(token));
    }

    public String getUsernameFromRefreshToken(String token) {
        return getClaim(token, Claims::getSubject, refreshTokenSecretKey);
    }

    public Long getRefreshTokenExpiresIn() {
        return refreshTokenExpiration / 1000;
    }

    private Date getRefreshTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, refreshTokenSecretKey);
    }

    private boolean isRefreshTokenExpired(String token) {
        return getRefreshTokenExpiration(token).before(new Date());
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromRefreshToken(token);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
    }
}
