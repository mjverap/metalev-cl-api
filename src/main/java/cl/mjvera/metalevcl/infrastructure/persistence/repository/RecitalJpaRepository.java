package cl.mjvera.metalevcl.infrastructure.persistence.repository;

import cl.mjvera.metalevcl.infrastructure.persistence.RecitalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecitalJpaRepository extends JpaRepository<RecitalEntity, Long>, JpaSpecificationExecutor<RecitalEntity> {
    boolean existsByVenue_Id(Long venueId);
}
