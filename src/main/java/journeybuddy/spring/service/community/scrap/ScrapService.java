package journeybuddy.spring.service.community.scrap;

import journeybuddy.spring.web.dto.community.scrap.ScrapResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ScrapService {
    Page<ScrapResponseDTO> findAll(String userEmail, Pageable pageable);
    ScrapResponseDTO saveScrap(String userEmail, Long postId);
    void deleteScrap(String userEmail, Long postId);
}
