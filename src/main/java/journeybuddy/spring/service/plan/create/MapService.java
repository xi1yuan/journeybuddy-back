package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CoordinateResponse;
import journeybuddy.spring.web.dto.plan.response.PlaceResponse;

import java.util.List;

public interface MapService {
    List<PlaceResponse> getPlace(String address);
    PlaceResponse getPlaceInfo(String address);
}
