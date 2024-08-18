package journeybuddy.spring.web.controller.plan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.plan.create.TourApiService;
import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;
import journeybuddy.spring.web.dto.plan.response.TourInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Travel Plan API", description = "여행 계획 생성 관련 API")
@Slf4j
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final TourApiService tourApiService;

    @GetMapping("/provinces")
    @Operation(summary = "특별시/도 코드, 이름 조회", description = "특별시/도 코드, 이름을 조회하는 API")
    public ResponseEntity<List<ProvinceResponse>> getProvinces() {
        List<ProvinceResponse> provinces = tourApiService.getProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/cities/{provinceCode}")
    @Operation(summary = "시/군/구 코드, 이름 조회", description = "특별시/도에 해당하는 시/군/구 코드, 이름을 조회하는 API")
    public ResponseEntity<List<CityResponse>> getCities(@RequestParam String provinceCode) {
        List<CityResponse> cities = tourApiService.getCities(provinceCode);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/tour-info")
    @Operation(summary = "관광 정보 조회", description = "특정 지역의 관광 정보를 조회하는 API \n areaCode, sigunguCode, preference를 입력받아 해당하는 관광 정보를 조회합니다." +
            "preference는 레포츠, 관광지, 문화시설, 숙박, 음식점이 있습니다.")
    public ResponseEntity<TourInfoResponse> getTourInfo(@RequestParam String areaCode, @RequestParam String sigunguCode, @RequestParam String preference) {

        TourInfoResponse tourInfoResponse = tourApiService.getTourInfo(areaCode, sigunguCode, preference);
        return ResponseEntity.ok(tourInfoResponse);
    }


}
