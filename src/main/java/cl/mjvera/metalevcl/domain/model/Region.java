package cl.mjvera.metalevcl.domain.model;

public class Region {
    private final Long id;
    private final String name;

    public Region(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
