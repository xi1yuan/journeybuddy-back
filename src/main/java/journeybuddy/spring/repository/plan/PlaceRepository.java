package journeybuddy.spring.repository.plan;

import journeybuddy.spring.domain.plan.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByAddress(String address);
}
