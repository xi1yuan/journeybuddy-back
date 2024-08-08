package journeybuddy.spring.service.community.scrap;

import journeybuddy.spring.domain.community.Scrap;
import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ScrapCommandService {
    Page<ScrapResponseDTO> findAll(String userEmail, Pageable pageable);
    Scrap saveScrap(String userEmail, Long postId, Scrap scrap);
}
