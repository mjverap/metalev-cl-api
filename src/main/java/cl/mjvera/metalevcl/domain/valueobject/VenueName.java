package cl.mjvera.metalevcl.domain.valueobject;

import cl.mjvera.metalevcl.domain.exception.InvalidRecitalInfoException;

public record VenueName(String value) {
    public VenueName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Venue name cannot be null or blank");
        }
    }
}
