package journeybuddy.spring.web.controller.plan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import journeybuddy.spring.service.plan.create.MapService;
import journeybuddy.spring.service.plan.create.PlanService;
import journeybuddy.spring.service.plan.create.TourApiService;
import journeybuddy.spring.service.plan.info.PlanInfoService;
import journeybuddy.spring.web.dto.plan.request.SavePlanRequest;
import journeybuddy.spring.web.dto.plan.request.TravelPlanRequest;
import journeybuddy.spring.web.dto.plan.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Travel Plan API", description = "여행 계획 관련 API")
@Slf4j
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final TourApiService tourApiService;
    private final MapService mapService;
    private final PlanService planService;
    private final PlanInfoService planInfoService;

    @GetMapping("/provinces")
    @Operation(summary = "특별시/도 코드, 이름 조회", description = "특별시/도 코드, 이름을 조회하는 API")
    public ResponseEntity<List<ProvinceResponse>> getProvinces() {
        List<ProvinceResponse> provinces = tourApiService.getProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/cities/{provinceCode}")
    @Operation(summary = "시/군/구 코드, 이름 조회", description = "특별시/도에 해당하는 시/군/구 코드, 이름을 조회하는 API")
    public ResponseEntity<List<CityResponse>> getCities(@PathVariable String provinceCode) {
        List<CityResponse> cities = tourApiService.getCities(provinceCode);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/tour-info")
    @Operation(summary = "관광 정보 조회", description = "특정 지역의 관광 정보를 조회하는 API. \n areaCode, sigunguCode, preference를 입력받아 해당하는 관광 정보를 조회합니다." +
            "preference는 레포츠, 관광지, 문화시설, 숙박, 음식점이 있습니다.")
    public ResponseEntity<TourInfoResponse> getTourInfo(@RequestParam String areaCode, @RequestParam String sigunguCode, @RequestParam String preference) {

        TourInfoResponse tourInfoResponse = tourApiService.getTourInfo(areaCode, sigunguCode, preference);
        return ResponseEntity.ok(tourInfoResponse);
    }

    @GetMapping("/search")
    @Operation(summary = "키워드로 장소 검색", description = "장소 검색 API / 장소 직접 추가시 사용")
    public ResponseEntity<List<PlaceResponse>> getPlace(@RequestParam String keyword) {
        List<PlaceResponse> placeResponse = mapService.getPlace(keyword);
        return ResponseEntity.ok(placeResponse);
    }

    @GetMapping("/place-info")
    @Operation(summary = "장소 정보 조회", description = "주소로 특정 장소의 정보 조회 API / 정보는 장소명, 주소, 장소 URL, 위도, 경도를 포함합니다.")
    public ResponseEntity<PlaceResponse> getPlaceInfo(@RequestParam String address) {
        PlaceResponse placeInfoResponse = mapService.getPlaceInfo(address);
        return ResponseEntity.ok(placeInfoResponse);
    }

    @PostMapping("/start-ai")
    @Operation(summary = "AI 여행 계획 생성", description = "AI를 활용한 여행 계획 생성 API / 선택된 장소들, 여행 일정 시작, 끝 날짜, 교통수단을 받아오면 해당 정보로 여행 계획 생성")
    public ResponseEntity<TravelPlanResponse> startAI(@RequestBody TravelPlanRequest travelPlanRequest) {
        TravelPlanResponse travelPlanResponse = planService.createPlan(travelPlanRequest);
        return ResponseEntity.ok(travelPlanResponse);
    }

    @PostMapping("/save")
    @Operation(summary = "여행 계획 저장", description = "여행 계획 저장 API / 생성된 여행 계획을 저장합니다.")
    public ResponseEntity<Map<String, Long>> savePlan(@RequestBody SavePlanRequest savePlanRequest, @AuthenticationPrincipal UserDetails userDetails) {
        Long planId = planService.savePlan(savePlanRequest, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("planId", planId));
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "여행 계획 삭제", description = "여행 계획 삭제 API / 저장된 여행 계획을 삭제합니다.")
    public ResponseEntity<Map<String, String>> deletePlan(@PathVariable Long planId, @AuthenticationPrincipal UserDetails userDetails) {
        planService.deletePlan(planId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "여행 계획 삭제 성공"));
    }

    @GetMapping("/plan-info/{planId}")
    @Operation(summary = "여행 계획 상세 조회", description = "여행 계획 상세 조회 API / 저장된 여행 계획의 상세 정보를 조회합니다.")
    public ResponseEntity<TravelPlanResponse> getPlanInfo(@PathVariable Long planId, @AuthenticationPrincipal UserDetails userDetails) {
        TravelPlanResponse travelPlanResponse = planInfoService.getPlanInfo(userDetails.getUsername(), planId);
        return ResponseEntity.ok(travelPlanResponse);
    }

}
