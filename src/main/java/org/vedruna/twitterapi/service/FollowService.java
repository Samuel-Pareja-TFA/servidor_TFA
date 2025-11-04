package org.vedruna.twitterapi.service;

public interface FollowService {
    /**
     * Hace que userId siga a toFollowId.
     */
    void followUser(Integer userId, Integer toFollowId);

    /**
     * Hace que userId deje de seguir a toUnfollowId.
     */
    void unfollowUser(Integer userId, Integer toUnfollowId);
}
