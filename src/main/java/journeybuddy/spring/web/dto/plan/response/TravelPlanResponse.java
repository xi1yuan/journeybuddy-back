package journeybuddy.spring.web.dto.plan.response;

import journeybuddy.spring.web.dto.plan.ScheduleDTO;
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
    private List<ScheduleDTO> schedules;

    @Builder
    public TravelPlanResponse(LocalDate startDate, LocalDate endDate, String transport, List<ScheduleDTO> schedules) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.transport = transport;
        this.schedules = schedules;
    }

}
