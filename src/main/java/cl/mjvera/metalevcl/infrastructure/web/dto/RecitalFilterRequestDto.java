package cl.mjvera.metalevcl.infrastructure.web.dto;

import cl.mjvera.metalevcl.domain.model.RecitalStatus;
import cl.mjvera.metalevcl.domain.model.RecitalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class RecitalFilterRequestDto {
    private RecitalType type;
    private RecitalStatus status;
    private Long venueId;
    private Integer minPrice;
    private Integer maxPrice;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDateTo;

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

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public LocalDate getEndDateTo() {
        return endDateTo;
    }

    public void setEndDateTo(LocalDate endDateTo) {
        this.endDateTo = endDateTo;
    }
}
