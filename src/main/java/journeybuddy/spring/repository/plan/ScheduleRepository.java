package journeybuddy.spring.repository.plan;

import journeybuddy.spring.domain.plan.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByPlan_PlanId(Long planId);
}
