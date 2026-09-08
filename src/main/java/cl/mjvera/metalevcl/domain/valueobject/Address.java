package cl.mjvera.metalevcl.domain.valueobject;

import cl.mjvera.metalevcl.domain.model.City;

public record Address(String street, City city) {
    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }
        if (city == null) {
            throw new IllegalArgumentException("City cannot be null");
        }
    }

    public String toString() {
        return street + ", " + city.getName() + ", " + city.getRegion().getName();
    }
}
