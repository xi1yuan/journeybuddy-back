package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CoordinateResponse;

public interface MapService {
    CoordinateResponse getCoordinate(String address);

}
