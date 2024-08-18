package journeybuddy.spring.web.dto.plan.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TravelPlanResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private String transport;
    private List<ScheduleDto> schedules;

    @Builder
    public TravelPlanResponse(LocalDate startDate, LocalDate endDate, String transport, List<ScheduleDto> schedules) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.transport = transport;
        this.schedules = schedules;
    }

    @Data
    public static class ScheduleDto {
        private String placeName;
        private String address;
        private String latitude;
        private String longitude;
        private LocalDateTime dateTime;
        private String transport;

        @Builder
        public ScheduleDto(String placeName, String address, String latitude, String longitude, LocalDateTime dateTime, String transport) {
            this.placeName = placeName;
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.dateTime = dateTime;
            this.transport = transport;
        }
    }

}
