package org.vedruna.twitterapi.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vedruna.twitterapi.persistance.entity.CommentEntity;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findByPublicationId(Integer publicationId);
}
