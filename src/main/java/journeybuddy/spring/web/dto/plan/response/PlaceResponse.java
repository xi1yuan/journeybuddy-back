package journeybuddy.spring.web.dto.plan.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PlaceResponse {
    private String address;
    private String url;
    private String name;
    private String longitude;
    private String latitude;

    @Builder
    public PlaceResponse(String address, String url, String name, String longitude, String latitude) {
        this.address = address;
        this.url = url;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
