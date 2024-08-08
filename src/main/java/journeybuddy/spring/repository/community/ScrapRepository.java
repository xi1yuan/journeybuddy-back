package journeybuddy.spring.repository.community;

import journeybuddy.spring.domain.user.User;
import journeybuddy.spring.domain.community.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Page<Scrap> findAllByUser(User user, Pageable pageable);
}
