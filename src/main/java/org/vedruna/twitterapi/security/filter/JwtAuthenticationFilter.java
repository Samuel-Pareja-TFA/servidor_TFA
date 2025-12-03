package org.vedruna.twitterapi.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.vedruna.twitterapi.security.service.JWTServiceImpl;

import io.jsonwebtoken.JwtException;

import java.io.IOException;

/**
 * Filtro de Spring Security que se ejecuta una vez por petición y se encarga de:
 *
 * <ul>
 *   <li>Extraer el Access Token de la cabecera {@code Authorization} (Bearer token).</li>
 *   <li>Validar el token usando {@link JWTServiceImpl}.</li>
 *   <li>Autenticar al usuario en el contexto de seguridad si el token es válido.</li>
 * </ul>
 *
 * <p>Este filtro permite que los endpoints protegidos con Spring Security
 * puedan identificar al usuario que realiza la petición a partir del JWT.</p>
 *
 * <p>La autenticación se realiza estableciendo un {@link UsernamePasswordAuthenticationToken}
 * en el {@link SecurityContextHolder} con los detalles del {@link UserDetails} cargado desde
 * {@link UserDetailsService}.</p>
 *
 * <p>Si no hay token, o el token es inválido/expirado, la petición continúa sin autenticación.</p>
 */
@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTServiceImpl jwtService;
    private final UserDetailsService userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Método principal del filtro que se ejecuta por cada petición HTTP.
     *
     * <p>Extrae el token de la cabecera, valida su integridad y caducidad,
     * y establece la autenticación en el contexto de seguridad si es válido.</p>
     *
     * @param request  la petición HTTP
     * @param response la respuesta HTTP
     * @param filterChain cadena de filtros para continuar la ejecución
     * @throws ServletException excepción de servlet
     * @throws IOException      excepción de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String username;
        try {
            username = jwtService.getUsernameFromAccessToken(token);
        } catch (JwtException e) {
            log.warn("Invalid/expired access token for {} : {}", request.getRequestURI(), e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            log.error("Unexpected error parsing token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isAccessTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authenticated user from token: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera {@code Authorization} de la petición.
     *
     * <p>Si la cabecera existe y comienza con {@code Bearer }, devuelve
     * únicamente la parte del token sin el prefijo.</p>
     *
     * @param request la petición HTTP
     * @return el JWT como {@link String}, o {@code null} si no existe o no es válido
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
