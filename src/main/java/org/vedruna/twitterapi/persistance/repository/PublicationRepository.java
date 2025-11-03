package org.vedruna.twitterapi.persistance.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vedruna.twitterapi.persistance.entity.PublicationEntity;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Integer> {

    // Todas las publicaciones de un usuario (paged)
    Page<PublicationEntity> findAllByUserId(Integer userId, Pageable pageable);

    // Todas las publicaciones cuyos autores estén en una lista (ej. usuarios que sigues)
    Page<PublicationEntity> findAllByUserIn(List<org.vedruna.twitterapi.persistance.entity.UserEntity> users, Pageable pageable);
}
