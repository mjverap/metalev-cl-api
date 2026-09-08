package cl.mjvera.metalevcl.domain.valueobject;
import java.util.List;

public record BandList(List<String> value) {
    public BandList {
        if (value == null) {
            throw new IllegalArgumentException("Band list cannot be null");
        }
        boolean hasInvalidBandName = value.stream().anyMatch(
                band -> band == null || band.isBlank() || band.trim().length() > 100
        );
        if (hasInvalidBandName) {
            throw new IllegalArgumentException("Band list cannot contain null, blank or too long names");
        }
        value = List.copyOf(value);
    }
}
