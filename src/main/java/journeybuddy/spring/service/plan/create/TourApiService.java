package journeybuddy.spring.service.plan.create;

import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;
import journeybuddy.spring.web.dto.plan.response.TourInfoResponse;

import java.util.List;

public interface TourApiService {
    List<ProvinceResponse> getProvinces();
    List<CityResponse> getCities(String provinceCode);
    TourInfoResponse getTourInfo(String areaCode, String sigunguCode, String preference);
}
