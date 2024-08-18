package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.request.TravelPlanRequest;
import journeybuddy.spring.web.dto.plan.response.TravelPlanResponse;

public interface PlanService {
//    SavePlanResponse savePlan(SavePlanRequest savePlanRequest);
    TravelPlanResponse createPlan(TravelPlanRequest travelPlanRequest);
}
