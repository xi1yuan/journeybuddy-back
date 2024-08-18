package journeybuddy.spring.web.dto.plan.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class CoordinateResponse {
    private double latitude;
    private double longitude;

    @Builder
    public CoordinateResponse(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
