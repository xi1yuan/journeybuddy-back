package journeybuddy.spring.web.dto.plan;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dateTime;
    private String transport;

    @Builder
    public ScheduleDTO(String placeName, String address, Double latitude, Double longitude, LocalDateTime dateTime, String transport) {
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
        this.transport = transport;
    }
}