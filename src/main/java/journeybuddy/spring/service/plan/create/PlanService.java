package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.request.SavePlanRequest;
import journeybuddy.spring.web.dto.plan.request.TravelPlanRequest;
import journeybuddy.spring.web.dto.plan.response.TravelPlanResponse;

public interface PlanService {
    Long savePlan(SavePlanRequest savePlanRequest, String userEmail);
    TravelPlanResponse createPlan(TravelPlanRequest travelPlanRequest);
}
