package cl.mjvera.metalevcl.infrastructure.persistence;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recitals")
public class RecitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;

    @ElementCollection
    @CollectionTable(name = "recital_bands", joinColumns = @JoinColumn(name = "recital_id"))
    @Column(name = "band_name", nullable = false)
    private List<String> bands = new ArrayList<>();

    private Integer minTicketPrice;
    private Integer maxTicketPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecitalType type = RecitalType.NATIONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecitalStatus status = RecitalStatus.UPCOMING;

    @Column
    private String recitalLink;

    protected RecitalEntity() {
    }

    public RecitalEntity(Long id, String name, VenueEntity venue, List<String> bands) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.bands = new ArrayList<>(bands);
    }

    public RecitalEntity(String name, VenueEntity venue, List<String> bands) {
        this(null, name, venue, bands);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VenueEntity getVenue() {
        return venue;
    }

    public void setVenue(VenueEntity venue) {
        this.venue = venue;
    }

    public List<String> getBands() {
        return new ArrayList<>(bands);
    }

    public void setBands(List<String> bands) {
        this.bands = new ArrayList<>(bands);
    }

    public Integer getMinTicketPrice() {
        return minTicketPrice;
    }

    public void setMinTicketPrice(Integer minTicketPrice) {
        this.minTicketPrice = minTicketPrice;
    }

    public Integer getMaxTicketPrice() {
        return maxTicketPrice;
    }

    public void setMaxTicketPrice(Integer maxTicketPrice) {
        this.maxTicketPrice = maxTicketPrice;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public RecitalType getType() {
        return type;
    }

    public void setType(RecitalType type) {
        this.type = type;
    }

    public RecitalStatus getStatus() {
        return status;
    }

    public void setStatus(RecitalStatus status) {
        this.status = status;
    }

    public String getRecitalLink() {
        return recitalLink;
    }

    public void setRecitalLink(String recitalLink) {
        this.recitalLink = recitalLink;
    }
}
