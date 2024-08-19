package journeybuddy.spring.web.dto.plan.request;

import journeybuddy.spring.web.dto.plan.PlaceDTO;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
public class TravelPlanRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String transport;
    private List<PlaceDTO> selectedPlaces;
}
