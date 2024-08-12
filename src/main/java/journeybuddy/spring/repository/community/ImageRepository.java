package journeybuddy.spring.repository.community;

import journeybuddy.spring.domain.community.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    void deleteAllByIdIn(List<Long> deletedImageIds);
}
