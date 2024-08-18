package journeybuddy.spring.web.dto.plan;

import lombok.Builder;
import lombok.Data;

@Data
public class PlaceDTO {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;

    @Builder
    public PlaceDTO(String name, String address, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
