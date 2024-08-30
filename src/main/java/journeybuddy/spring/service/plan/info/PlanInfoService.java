package journeybuddy.spring.service.plan.info;

import journeybuddy.spring.web.dto.plan.response.PlanListResponse;
import journeybuddy.spring.web.dto.plan.response.TravelPlanResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlanInfoService {
    TravelPlanResponse getPlanInfo(String userEmail, Long planId);
    List<PlanListResponse> getPlanList(String userEmail, Pageable pageable);
}
