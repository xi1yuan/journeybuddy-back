package journeybuddy.spring.service.plan.create;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.plan.Schedule;
import journeybuddy.spring.domain.plan.Place;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.plan.PlanRepository;
import journeybuddy.spring.repository.plan.PlaceRepository;
import journeybuddy.spring.repository.plan.ScheduleRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.plan.PlaceDTO;
import journeybuddy.spring.web.dto.plan.ScheduleDTO;
import journeybuddy.spring.web.dto.plan.request.SavePlanRequest;
import journeybuddy.spring.web.dto.plan.request.TravelPlanRequest;
import journeybuddy.spring.web.dto.plan.response.TravelPlanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final ScheduleRepository scheduleRepository;
    private final PlaceRepository placeRepository;

    // AI 기반 여행 계획 생성
    public TravelPlanResponse createPlan(TravelPlanRequest travelPlanRequest) {
        String aiResponse = openAIService.generateTravelPlan(buildPrompt(travelPlanRequest));
        log.info("AI response: " + aiResponse);
        return parseTravelPlanResponse(aiResponse, travelPlanRequest);
    }

    // 여행 계획 저장
    @Transactional
    public Long savePlan(SavePlanRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        Plan plan = buildAndSavePlan(request, user);
        saveSchedules(request, plan);
        return plan.getPlanId();
    }

    // 여행 계획 삭제
    @Transactional
    public void deletePlan(Long planId, String userEmail) {
        // 해당 유저가 생성한 계획이 아니면 오류 발생
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));
        if(!planRepository.existsByPlanIdAndUser(planId, user)) {
            throw new IllegalArgumentException("해당 user의 계획이 아닙니다: " + planId);
        }


        Plan plan = planRepository.findById(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
        planRepository.delete(plan);
    }

    private String buildPrompt(TravelPlanRequest travelPlanRequest) {
        List<PlaceDTO> selectedPlaces = travelPlanRequest.getSelectedPlaces();
        StringBuilder prompt = new StringBuilder();
        prompt.append("이제부터 너는 최적 여행 계획 설계자야. 지금부터 내가 너에게 제공하는건 한국을 여행하는데 내가 가고 싶은 장소들 정보야. 내가 주는 장소들의 한해서 장소, 일정, 교통수단을 고려하여 최적의 여행 경로를 계획해 줘.\n\n");
        prompt.append("가고 싶은 장소:\n");
        for (PlaceDTO place : selectedPlaces) {
            prompt.append("장소명: ").append(place.getName()).append("\n");
            prompt.append("주소: ").append(place.getAddress()).append("\n");
            prompt.append("위도: ").append(place.getLatitude()).append("\n");
            prompt.append("경도: ").append(place.getLongitude()).append("\n\n");
        }

        prompt.append("\n여행 일정:\n");
        prompt.append("시작일: ").append(travelPlanRequest.getStartDate()).append("\n");
        prompt.append("종료일: ").append(travelPlanRequest.getEndDate()).append("\n");
        prompt.append("교통수단: ").append(travelPlanRequest.getTransport()).append("\n\n");
        prompt.append("계획에 위에 있는 장소를 전부 포함하도록 노력은 하되, 너무 많은 장소들로 인해 힘들 수도 있으니까 일정과 머무는 시간을 고려해서 스케쥴 상 무리일 것 같은 것은 제외해. 각 장소를 방문하는 일정을 거리와 교통 수단에 맞춰서 적절하게 분배하여 시간대별(예: 09:00, 14:00 등)로 제시해. 각 장소 간의 이동 시간을 꼭 고려하고 갔던 곳 재방문은 절대 안돼.\n");
        prompt.append("장소에 대한 주소 정보와 위도, 경도를 보고 어떤 장소를 어떤 순서로 몇시에 방문하는게 좋을지 알려줘\n");
        prompt.append("또한 해당 장소에 대한 정보를 검색해서 어떤 시간에 사람들이 주로 그 장소에 방문하는지, 그 장소에서 주로 얼마나 머무는지를 파악해서 적절하게 계획 세워줘.\n");
        prompt.append("응답은 반드시 아래와 예시 형식과 똑같은 JSON 형식으로만 해. json 답만하고 다른 대답은 하지마\n\n");
        prompt.append("{\n");
        prompt.append("  \"schedule\": [\n");
        prompt.append("    {\"dateTime\": \"YYYY-MM-DDTHH:MM:SS\", \"name\": \"경복궁\", \"address\": \"서울특별시 종로구 사직로 161\", \"latitude\": \"37.579617\", \"longitude\": \"126.977041\", \"transport\": 택시},\n");
        prompt.append("    {\"dateTime\": \"YYYY-MM-DDTHH:MM:SS\", \"name\": \"북촌 한옥마을\", \"address\": \"서울특별시 종로구 계동길 37\", \"latitude\": \"37.582604\", \"longitude\": \"126.983648\", \"transport\": 대중교통}\n");
        prompt.append("  ]\n");
        prompt.append("}\n");

        return prompt.toString();
    }

    private TravelPlanResponse parseTravelPlanResponse(String json, TravelPlanRequest request) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(json, new TypeReference<>() {});
            List<ScheduleDTO> schedules = extractSchedules(responseMap);
            return TravelPlanResponse.builder()
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .transport(request.getTransport())
                    .schedules(schedules)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse the AI response: " + e.getMessage(), e);
        }
    }

    private List<ScheduleDTO> extractSchedules(Map<String, Object> responseMap) {
        List<Map<String, Object>> schedulesMap = (List<Map<String, Object>>) responseMap.get("schedule");
        List<ScheduleDTO> schedules = new ArrayList<>();
        for (Map<String, Object> entry : schedulesMap) {
            schedules.add(ScheduleDTO.builder()
                    .dateTime(LocalDateTime.parse((String) entry.get("dateTime")))
                    .placeName((String) entry.get("name"))
                    .address((String) entry.get("address"))
                    .transport((String) entry.get("transport"))
                    .latitude(Double.parseDouble(entry.get("latitude").toString()))
                    .longitude(Double.parseDouble(entry.get("longitude").toString()))
                    .build());
        }
        return schedules;
    }

    private Plan buildAndSavePlan(SavePlanRequest request, User user) {
        Plan plan = Plan.builder()
                .name(request.getPlanName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .transport(request.getTransport())
                .user(user)
                .build();
        return planRepository.save(plan);
    }

    private void saveSchedules(SavePlanRequest request, Plan plan) {
        for (ScheduleDTO dto : request.getSchedules()) {
            Place place = findOrCreatePlace(dto.getAddress(), dto.getPlaceName(), dto.getLatitude(), dto.getLongitude());
            Schedule schedule = Schedule.builder()
                    .dateTime(dto.getDateTime())
                    .place(place)
                    .plan(plan)
                    .transport(dto.getTransport())
                    .build();
            scheduleRepository.save(schedule);
        }
    }

    private Place findOrCreatePlace(String address, String name, Double latitude, Double longitude) {
        return placeRepository.findByAddress(address)
                .orElseGet(() -> placeRepository.save(Place.builder()
                        .address(address)
                        .name(name)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build()));
    }
}
