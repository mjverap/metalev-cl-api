package cl.mjvera.metalevcl.domain.model;

import cl.mjvera.metalevcl.domain.valueobject.Address;
import cl.mjvera.metalevcl.domain.valueobject.VenueName;

public class Venue {
    private final Long id;
    private String name;
    private Address address;

    public Venue(Long id, String name, Address address) {
        this.id = id;
        this.name = new VenueName(name).value();
        this.address = address;
    }

    public Venue(String name, Address address) {
        this(null, name, address);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = new VenueName(name).value();
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void renameTo(String newName) {
        this.name = new VenueName(newName).value();
    }

    public void updateAddress(Address newAddress) {
        this.address = newAddress;
    }
}
