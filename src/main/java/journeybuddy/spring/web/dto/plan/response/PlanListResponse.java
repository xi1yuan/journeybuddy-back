package journeybuddy.spring.web.dto.plan.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlanListResponse {
    private Long planId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String transport;
}
