package cl.mjvera.metalevcl.infrastructure.persistence.repository;

import cl.mjvera.metalevcl.infrastructure.persistence.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueJpaRepository extends JpaRepository<VenueEntity, Long> {
    boolean existsBy();

    @Query("""
            SELECT v
            FROM VenueEntity v
            JOIN v.city c
            JOIN c.region r
            WHERE (:region IS NULL OR LOWER(r.name) = :region)
              AND (:city IS NULL OR LOWER(c.name) = :city)
            """)
    List<VenueEntity> findByRegionAndCity(@Param("region") String region, @Param("city") String city);
}
