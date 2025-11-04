package org.vedruna.twitterapi.controller;

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

* Controlador para seguir y dejar de seguir usuarios.
  */
  @RestController
  @RequestMapping("/users")
  @RequiredArgsConstructor
  public class FollowController {

  private final FollowService followService;
  private final UserRepository userRepository;

  /**

  * Seguir a un usuario.
    */
    @PostMapping("/{userId}/follow/{toFollowId}")
    public ResponseEntity<String> followUser(
    @PathVariable Integer userId,
    @PathVariable Integer toFollowId) {

    followService.followUser(userId, toFollowId);
    return ResponseEntity.ok("User " + userId + " sigue ahora a " + toFollowId);
    }

  /**

  * Dejar de seguir a un usuario.
    */
    @DeleteMapping("/{userId}/follow/{toUnfollowId}")
    public ResponseEntity<String> unfollowUser(
    @PathVariable Integer userId,
    @PathVariable Integer toUnfollowId) {

    followService.unfollowUser(userId, toUnfollowId);
    return ResponseEntity.ok("User " + userId + " dejó de seguir a " + toUnfollowId);
    }

  /**

  * Obtener todos los usuarios que sigue un usuario.
    */
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
    */
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

  * Convierte UserEntity a UserDto.
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
