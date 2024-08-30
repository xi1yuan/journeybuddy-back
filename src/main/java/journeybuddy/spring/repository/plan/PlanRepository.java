package journeybuddy.spring.repository.plan;

import journeybuddy.spring.domain.plan.Plan;
import journeybuddy.spring.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    boolean existsByPlanIdAndUser(Long planId, User user);

    List<Plan> findByUser(User user, Pageable pageable);
}
