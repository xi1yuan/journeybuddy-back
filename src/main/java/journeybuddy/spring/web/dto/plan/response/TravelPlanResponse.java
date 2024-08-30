package journeybuddy.spring.web.dto.plan.response;

import journeybuddy.spring.web.dto.plan.ScheduleDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TravelPlanResponse {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String transport;
    private List<ScheduleDTO> schedules;

    @Builder
    public TravelPlanResponse(String name, LocalDate startDate, LocalDate endDate, String transport, List<ScheduleDTO> schedules) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.transport = transport;
        this.schedules = schedules;
    }

}
