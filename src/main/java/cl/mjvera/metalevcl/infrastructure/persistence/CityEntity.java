package cl.mjvera.metalevcl.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "cities",
        uniqueConstraints = @UniqueConstraint(name = "uk_cities_region_name", columnNames = {"region_id", "name"})
)
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private RegionEntity region;

    protected CityEntity() {
    }

    public CityEntity(Long id, String name, RegionEntity region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    public CityEntity(String name, RegionEntity region) {
        this(null, name, region);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RegionEntity getRegion() {
        return region;
    }
}
