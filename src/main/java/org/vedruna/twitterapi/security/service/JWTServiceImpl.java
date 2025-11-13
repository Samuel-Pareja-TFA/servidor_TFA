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
 * Servicio para generación, extracción y validación de JWT (Access Token y Refresh Token).
 *
 * <p>Proporciona métodos para:</p>
 * <ul>
 *   <li>Generar Access Token y Refresh Token para un usuario dado.</li>
 *   <li>Extraer información de los tokens (claims y username).</li>
 *   <li>Validar tokens y comprobar si han expirado.</li>
 * </ul>
 *
 * <p>Se integra con {@link UserEntity} para obtener información del usuario y con
 * {@link UserDetails} para la validación en Spring Security.</p>
 *
 * <p>Los secretos y tiempos de expiración se inyectan mediante {@code @Value} desde
 * {@code application.properties}.</p>
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

    /**
     * Genera la clave secreta a partir de la cadena codificada en Base64.
     *
     * @param secretKey clave secreta en Base64
     * @return {@link SecretKey} usable por JJWT
     */
    private SecretKey getKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Construye un token JWT a partir de claims, usuario, tiempo de expiración y clave secreta.
     *
     * @param extraClaims claims adicionales a incluir
     * @param user usuario para establecer como subject
     * @param expirationTime tiempo de expiración en milisegundos
     * @param secretKey clave secreta para firmar el token
     * @return token JWT como {@link String}
     */
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

    /**
     * Genera un Access Token para un usuario.
     *
     * @param user usuario del que se genera el token
     * @return Access Token JWT
     */
    public String generateAccessToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        return buildToken(claims, user, accessTokenExpiration, accessTokenSecretKey);
    }

    /**
     * Genera un Refresh Token para un usuario.
     *
     * @param user usuario del que se genera el token
     * @return Refresh Token JWT
     */
    public String generateRefreshToken(UserEntity user) {
        return buildToken(new HashMap<>(), user, refreshTokenExpiration, refreshTokenSecretKey);
    }

    /**
     * Extrae todos los claims de un token usando la clave secreta correspondiente.
     *
     * <p>Se usa Jwts.parser() como fallback para compatibilidad con distintas versiones
     * de JJWT.</p>
     *
     * @param token token JWT
     * @param secretKey clave secreta
     * @return claims del token
     */
    private Claims getAllClaims(String token, String secretKey) {
    // Llamamos build() en el builder para obtener el JwtParser y luego parseamos.
    return Jwts.parser()
            .setSigningKey(getKey(secretKey))
            .build()                       // <- IMPORTANTE: construir el parser aquí
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Extrae un claim específico del token usando una función resolver.
     *
     * @param <T> tipo de dato del claim
     * @param token token JWT
     * @param claimsResolver función para extraer el claim
     * @param secretKey clave secreta
     * @return valor del claim
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = getAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene el username desde un Access Token.
     *
     * @param token Access Token
     * @return username contenido en el token
     */
    public String getUsernameFromAccessToken(String token) {
        return getClaim(token, Claims::getSubject, accessTokenSecretKey);
    }

    /**
     * Devuelve la duración del Access Token en segundos.
     *
     * @return expiración en segundos
     */
    public Long getAccessTokenExpiresIn() {
        return accessTokenExpiration / 1000;
    }

    /**
     * Obtiene la fecha de expiración del Access Token.
     * @param token
     * @return
     */
    private Date getAccessTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, accessTokenSecretKey);
    }

    /**
     * Determina si el Access Token ha expirado.
     * @param token
     * @return
     */
    private boolean isAccessTokenExpired(String token) {
        return getAccessTokenExpiration(token).before(new Date());
    }

    /**
     * Valida un Access Token contra un usuario.
     *
     * @param token token JWT a validar
     * @param userDetails detalles del usuario
     * @return true si el token es válido y no ha expirado
     */
    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromAccessToken(token);
        return (username.equals(userDetails.getUsername()) && !isAccessTokenExpired(token));
    }

    /**
     * Obtiene el username desde un Refresh Token.
     *
     * @param token Refresh Token
     * @return username contenido en el token
     */
    public String getUsernameFromRefreshToken(String token) {
        return getClaim(token, Claims::getSubject, refreshTokenSecretKey);
    }

    /**
     * Devuelve la duración del Refresh Token en segundos.
     *
     * @return expiración en segundos
     */
    public Long getRefreshTokenExpiresIn() {
        return refreshTokenExpiration / 1000;
    }

    /**
     * Obtiene la fecha de expiración del Refresh Token.
     * @param token
     * @return
     */
    private Date getRefreshTokenExpiration(String token) {
        return getClaim(token, Claims::getExpiration, refreshTokenSecretKey);
    }

    /**
     * Determina si el Refresh Token ha expirado.
     * @param token
     * @return
     */
    private boolean isRefreshTokenExpired(String token) {
        return getRefreshTokenExpiration(token).before(new Date());
    }

    /**
     * Valida un Refresh Token contra un usuario.
     *
     * @param token token JWT a validar
     * @param userDetails detalles del usuario
     * @return true si el token es válido y no ha expirado
     */
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromRefreshToken(token);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
    }
}
