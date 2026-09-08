package cl.mjvera.metalevcl.domain.model;

import cl.mjvera.metalevcl.domain.exception.InvalidDateRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidPriceRangeException;
import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;
import cl.mjvera.metalevcl.domain.valueobject.BandList;
import cl.mjvera.metalevcl.domain.valueobject.DateRange;
import cl.mjvera.metalevcl.domain.valueobject.PriceRange;
import cl.mjvera.metalevcl.domain.valueobject.RecitalName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Recital {
    private final Long id;
    private String name;
    private Venue venue;
    private final List<String> bands;
    private PriceRange ticketPriceRange;
    private DateRange dateRange;
    private RecitalType type;
    private RecitalStatus status;
    private String recitalLink;

    public Recital() {
        this.id = null;
        this.name = "";
        this.venue = null;
        this.bands = new ArrayList<>();
        this.type = RecitalType.NATIONAL;
        this.status = RecitalStatus.UPCOMING;
    }

    public Recital(String name, Venue venue, List<String> bands) {
        if (venue == null) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        try {
            this.id = null;
            this.name = new RecitalName(name).value();
            this.venue = venue;
            this.bands = new ArrayList<>(new BandList(bands).value());
            this.type = RecitalType.NATIONAL;
            this.status = RecitalStatus.UPCOMING;
        } catch (IllegalArgumentException exception) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
    }

    public Recital(
            Long id,
            String name,
            Venue venue,
            List<String> bands,
            int minTicketPrice, int maxTicketPrice,
            LocalDate startDate, LocalDate endDate,
            RecitalType type,
            RecitalStatus status,
            String recitalLink) {
        if (venue == null) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        try {
            this.id = id;
            this.name = new RecitalName(name).value();
            this.venue = venue;
            this.bands = new ArrayList<>(new BandList(bands).value());
            this.ticketPriceRange = new PriceRange(minTicketPrice, maxTicketPrice);
            this.dateRange = new DateRange(startDate, endDate);
            this.type = type == null ? RecitalType.NATIONAL : type;
            this.status = status == null ? RecitalStatus.UPCOMING : status;
            this.recitalLink = recitalLink;
        } catch (IllegalArgumentException exception) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
    }

    public Recital(Long id, String name, Venue venue, List<String> bands) {
        this(
                id,
                name,
                venue,
                bands,
                0,
                0,
                LocalDate.now(),
                LocalDate.now(),
                RecitalType.NATIONAL,
                RecitalStatus.UPCOMING,
                null
        );
        this.ticketPriceRange = null;
        this.dateRange = null;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = new RecitalName(name).value();
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        if (venue == null) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        this.venue = venue;
    }

    public List<String> getBands() {
        return new ArrayList<>(bands);
    }

    public void setBands(List<String> bands) {
        this.bands.clear();
        this.bands.addAll(new BandList(bands).value());
    }

    public PriceRange getTicketPriceRange() {
        return this.ticketPriceRange;
    }

    public int getMinTicketPrice() {
        return this.ticketPriceRange == null ? 0 : this.ticketPriceRange.minPrice();
    }

    public int getMaxTicketPrice() {
        return this.ticketPriceRange == null ? 0 : this.ticketPriceRange.maxPrice();
    }

    public DateRange getDateRange() {
        return this.dateRange;
    }

    public RecitalType getType() {
        return type;
    }

    public void setType(RecitalType type) {
        this.type = type == null ? RecitalType.NATIONAL : type;
    }

    public RecitalStatus getStatus() {
        return status;
    }

    public void setStatus(RecitalStatus status) {
        this.status = status == null ? RecitalStatus.UPCOMING : status;
    }

    public String getRecitalLink() {
        return recitalLink;
    }

    public void setRecitalLink(String recitalLink) {
        this.recitalLink = recitalLink;
    }

    public void setTicketPriceRange(int minTicketPrice, int maxTicketPrice) {
        updateTicketPriceRange(minTicketPrice, maxTicketPrice);
    }

    public LocalDate getStartDate() {
        return this.dateRange == null ? null : this.dateRange.startDate();
    }

    public LocalDate getEndDate() {
        return this.dateRange == null ? null : this.dateRange.endDate();
    }

    public void setDateRange(LocalDate startDate, LocalDate endDate) {
        reprogramTo(startDate, endDate);
    }

    public void renameTo(String newName) {
        this.name = new RecitalName(newName).value();
    }

    public void moveTo(Venue newVenue) {
        if (newVenue == null) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        this.venue = newVenue;
    }

    public void addBand(String bandName) {
        if (bandName == null || bandName.isBlank()) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        this.bands.add(bandName);
    }

    public void removeBand(String bandName) {
        if (bandName == null || bandName.isBlank()) {
            throw new InvalidRecitalInfoException("Invalid recital info.");
        }
        this.bands.remove(bandName);
    }

    public void updateTicketPriceRange(int minTicketPrice, int maxTicketPrice) {
        try {
            this.ticketPriceRange = new PriceRange(minTicketPrice, maxTicketPrice);
        } catch (IllegalArgumentException exception) {
            throw new InvalidPriceRangeException("Minimum ticket price cannot be greater that maximum ticket price.");
        }
    }

    public void reprogramTo(LocalDate startDate, LocalDate endDate) {
        try {
            this.dateRange = new DateRange(startDate, endDate);
        } catch (IllegalArgumentException exception) {
            throw new InvalidDateRangeException("Start date cannot be after end date.");
        }
    }
}
