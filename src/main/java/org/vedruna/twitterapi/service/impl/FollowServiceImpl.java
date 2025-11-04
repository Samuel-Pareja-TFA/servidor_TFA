package org.vedruna.twitterapi.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vedruna.twitterapi.persistance.entity.UserEntity;
import org.vedruna.twitterapi.persistance.repository.UserRepository;
import org.vedruna.twitterapi.service.FollowService;
import org.vedruna.twitterapi.service.exception.FollowNotFoundException;
import org.vedruna.twitterapi.service.exception.UserNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementación simple de FollowService que manipula las colecciones ManyToMany
 * en la entidad UserEntity (following / followers).
 */
@Slf4j
@AllArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void followUser(Integer userId, Integer toFollowId) {
        log.info("User {} intenta seguir a {}", userId, toFollowId);
        if (userId.equals(toFollowId)) {
            throw new FollowNotFoundException("Cannot follow yourself");
        }

        UserEntity me = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserEntity other = userRepository.findById(toFollowId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + toFollowId));

        Set<UserEntity> following = me.getFollowing() == null ? new HashSet<>() : me.getFollowing();
        if (!following.contains(other)) {
            following.add(other);
            me.setFollowing(following);
            // persistimos el cambio (dueño de relación es la tabla join definida en UserEntity)
            userRepository.save(me);
            log.info("User {} ahora sigue a {}", userId, toFollowId);
        } else {
            log.info("User {} ya seguía a {}", userId, toFollowId);
        }
    }

    @Override
    @Transactional
    public void unfollowUser(Integer userId, Integer toUnfollowId) {
        log.info("User {} intenta dejar de seguir a {}", userId, toUnfollowId);
        UserEntity me = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + userId));
        UserEntity other = userRepository.findById(toUnfollowId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + toUnfollowId));

        Set<UserEntity> following = me.getFollowing();
        if (following == null || !following.remove(other)) {
            throw new FollowNotFoundException("Follow relationship not found between " + userId + " and " + toUnfollowId);
        }
        me.setFollowing(following);
        userRepository.save(me);
        log.info("User {} dejó de seguir a {}", userId, toUnfollowId);
    }
}

