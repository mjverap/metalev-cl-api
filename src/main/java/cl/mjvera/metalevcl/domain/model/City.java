package cl.mjvera.metalevcl.domain.model;

public class City {
    private final Long id;
    private final String name;
    private final Region region;

    public City(Long id, String name, Region region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Region getRegion() {
        return region;
    }
}
