package journeybuddy.spring.web.controller.plan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.plan.create.TravelApiService;
import journeybuddy.spring.web.dto.plan.response.CityResponse;
import journeybuddy.spring.web.dto.plan.response.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Travel Plan API", description = "여행 계획 생성 관련 API")
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final TravelApiService travelApiService;

    @GetMapping("/provinces")
    @Operation(summary = "특별시/도 코드, 이름 조회", description = "특별시/도 코드, 이름을 조회하는 API")
    public ResponseEntity<List<ProvinceResponse>> getProvinces() {
        List<ProvinceResponse> provinces = travelApiService.getProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/cities/{provinceCode}")
    @Operation(summary = "시/군/구 코드, 이름 조회", description = "특별시/도에 해당하는 시/군/구 코드, 이름을 조회하는 API")
    public ResponseEntity<List<CityResponse>> getCities(@RequestParam String provinceCode) {
        List<CityResponse> cities = travelApiService.getCities(provinceCode);
        return ResponseEntity.ok(cities);
    }

}
