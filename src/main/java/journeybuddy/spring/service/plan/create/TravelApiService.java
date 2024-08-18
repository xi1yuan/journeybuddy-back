package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;

import java.util.List;

public interface TravelApiService {
    List<ProvinceResponse> getProvinces();
    List<CityResponse> getCities(String provinceCode);
//    List<TourInfoResponse> getTourInfo(TourInfoRequest request);
}
