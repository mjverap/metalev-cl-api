package cl.mjvera.metalevcl.infrastructure.persistence.repository;

import cl.mjvera.metalevcl.infrastructure.persistence.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionJpaRepository extends JpaRepository<RegionEntity, Long> {
    Optional<RegionEntity> findByNameIgnoreCase(String name);
}
