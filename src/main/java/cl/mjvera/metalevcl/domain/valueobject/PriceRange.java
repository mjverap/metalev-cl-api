package cl.mjvera.metalevcl.domain.valueobject;

public record PriceRange(int minPrice, int maxPrice) {
    public PriceRange {
        if (minPrice < 0 || maxPrice < 0) {
            throw new IllegalArgumentException("Ticket prices cannot be negative");
        }
        if (minPrice > maxPrice) {
            throw new IllegalArgumentException("Minimum ticket price cannot be greater than maximum ticket price");
        }
    }
}
