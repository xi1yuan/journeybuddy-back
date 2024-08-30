package journeybuddy.spring.service.plan.info;

import journeybuddy.spring.domain.plan.Place;
import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.plan.Schedule;
import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.repository.plan.PlaceRepository;
import journeybuddy.spring.repository.plan.PlanRepository;
import journeybuddy.spring.repository.plan.ScheduleRepository;
import journeybuddy.spring.repository.user.UserRepository;
import journeybuddy.spring.web.dto.plan.response.PlanListResponse;
import journeybuddy.spring.web.dto.plan.response.TravelPlanResponse;
import journeybuddy.spring.web.dto.plan.ScheduleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanInfoServiceImpl implements PlanInfoService {

    private final PlanRepository planRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    // 여행 계획 상세 조회
    public TravelPlanResponse getPlanInfo(String userId, Long planId) {

        // 해당 계획이 존재하지 않으면 오류 발생
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        // 해당 계획의 일정 조회
        List<Schedule> schedules = scheduleRepository.findByPlan_PlanId(planId);

        // Schedule에서 Place 정보를 가져와 ScheduleDTO로 변환
        List<ScheduleDTO> scheduleDTOs = schedules.stream().map(schedule -> {
            Place place = schedule.getPlace();
            return ScheduleDTO.builder()
                    .placeName(place.getName())
                    .address(place.getAddress())
                    .latitude(place.getLatitude())
                    .longitude(place.getLongitude())
                    .dateTime(schedule.getDateTime())
                    .transport(schedule.getTransport())
                    .build();
        }).collect(Collectors.toList());

        // TravelPlanResponse를 생성하여 반환
        return TravelPlanResponse.builder()
                .name(plan.getName())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .transport(plan.getTransport())
                .schedules(scheduleDTOs)
                .build();
    }

    // 여행 계획 목록 조회
    public List<PlanListResponse> getPlanList(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        // 해당 유저의 계획 목록 조회
        List<Plan> plans = planRepository.findByUser(user, pageable);

        // PlanListResponse로 변환하여 반환
        return plans.stream().map(plan -> PlanListResponse.builder()
                .planId(plan.getPlanId())
                .name(plan.getName())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .transport(plan.getTransport())
                .build())
                .collect(Collectors.toList());
    }
}
