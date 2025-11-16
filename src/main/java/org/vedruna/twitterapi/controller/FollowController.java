package org.vedruna.twitterapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vedruna.twitterapi.controller.dto.UserDto;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.FollowService;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

  /**
   * Controlador REST para gestionar las relaciones de seguimiento (follow/unfollow)
   * entre usuarios.
   *
   * <p>Permite:
   * <ul>
   *   <li>Seguir a un usuario</li>
   *   <li>Dejar de seguir a un usuario</li>
   *   <li>Obtener todos los usuarios que sigue un usuario</li>
   *   <li>Obtener todos los usuarios que siguen a un usuario</li>
   * </ul>
   *
   * <p>Todos los métodos exponen {@link ResponseEntity} con código HTTP adecuado.
   * Las excepciones como {@link UserNotFoundException} se manejan en capa de servicio
   * y se pueden mapear a HTTP 404 mediante un {@code @ControllerAdvice}.
   *
   * <p>La conversión de {@link UserEntity} a {@link UserDto} se realiza internamente
   * para no exponer información sensible como contraseñas.
   */
  @RestController
  @RequestMapping("/api/v1/follows")
  @RequiredArgsConstructor
  public class FollowController {

      private final FollowService followService;
      private final UserRepository userRepository;

    /**
     * Seguir a un usuario.
     *
     * @param userId ID del usuario que desea seguir
     * @param toFollowId ID del usuario que será seguido
     * @return {@link ResponseEntity} con mensaje de confirmación
     */
      @Operation(summary = "Seguir a un usuario", description = "Permite que un usuario siga a otro usuario")
      @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Usuario seguido correctamente"),
          @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
      @PostMapping("/{userId}/follow/{toFollowId}")
      public ResponseEntity<String> followUser(
              @PathVariable Integer userId,
              @PathVariable Integer toFollowId) {

          followService.followUser(userId, toFollowId);
          return ResponseEntity.ok("User " + userId + " sigue ahora a " + toFollowId);
      }

    /**
     * Dejar de seguir a un usuario.
     *
     * @param userId ID del usuario que deja de seguir
     * @param toUnfollowId ID del usuario que será dejado de seguir
     * @return {@link ResponseEntity} con mensaje de confirmación
     */
      @Operation(summary = "Dejar de seguir a un usuario", description = "Permite que un usuario deje de seguir a otro usuario")
      @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Usuario dejado de seguir correctamente"),
          @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
      @DeleteMapping("/{userId}/follow/{toUnfollowId}")
      public ResponseEntity<String> unfollowUser(
              @PathVariable Integer userId,
              @PathVariable Integer toUnfollowId) {

          followService.unfollowUser(userId, toUnfollowId);
          return ResponseEntity.ok("User " + userId + " dejó de seguir a " + toUnfollowId);
      }

    /**
     * Obtener todos los usuarios que sigue un usuario.
     *
     * @param userId ID del usuario
     * @return {@link ResponseEntity} con lista de {@link UserDto} de los usuarios seguidos
     */
      @Operation(summary = "Obtener usuarios que sigue un usuario", description = "Devuelve la lista de usuarios que sigue un usuario")
      @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Lista de usuarios seguida obtenida correctamente"),
          @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
      @GetMapping("/{userId}/following")
      public ResponseEntity<List<UserDto>> getFollowing(@PathVariable Integer userId) {
          UserEntity user = userRepository.findById(userId)
                  .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + userId + " no encontrado"));
          Set<UserEntity> following = user.getFollowing();

          List<UserDto> dtos = following.stream()
                  .map(this::convertToDto)
                  .collect(Collectors.toList());
          return ResponseEntity.ok(dtos);
      }

    /**
     * Obtener todos los usuarios que siguen a un usuario.
     *
     * @param userId ID del usuario
     * @return {@link ResponseEntity} con lista de {@link UserDto} de los followers
     */
      @Operation(summary = "Obtener usuarios que siguen a un usuario", description = "Devuelve la lista de seguidores de un usuario")
      @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Lista de seguidores obtenida correctamente"),
          @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
      @GetMapping("/{userId}/followers")
      public ResponseEntity<List<UserDto>> getFollowers(@PathVariable Integer userId) {
          UserEntity user = userRepository.findById(userId)
                  .orElseThrow(() -> new UserNotFoundException("Usuario con ID " + userId + " no encontrado"));
          Set<UserEntity> followers = user.getFollowers();

          List<UserDto> dtos = followers.stream()
                  .map(this::convertToDto)
                  .collect(Collectors.toList());
          return ResponseEntity.ok(dtos);
      }

     /**
     * Convierte un {@link UserEntity} a {@link UserDto}.
     *
     * @param user entidad de usuario
     * @return DTO correspondiente
     */
    private UserDto convertToDto(UserEntity user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDescription(user.getDescription());
        dto.setCreateDate(user.getCreateDate());
        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getName());
        }
        return dto;
    }
}
