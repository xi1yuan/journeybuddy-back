package journeybuddy.spring.repository.plan;

import journeybuddy.spring.domain.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
