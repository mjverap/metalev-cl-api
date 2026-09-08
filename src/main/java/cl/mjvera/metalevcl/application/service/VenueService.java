package cl.mjvera.metalevcl.application.service;

import cl.mjvera.metalevcl.domain.model.Venue;

import java.util.List;

public interface VenueService {
    List<Venue> getVenues();
    List<Venue> getVenues(String region, String city);
    Venue getVenueById(Long id);
    Venue createVenue(String name, String street, Long cityId);
    Venue updateVenueName(Long id, String name);
    Venue updateVenueAddress(Long id, String street, Long cityId);
    void deleteVenue(Long id);
}
