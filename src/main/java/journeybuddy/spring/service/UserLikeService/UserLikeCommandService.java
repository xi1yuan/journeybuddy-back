package journeybuddy.spring.service.UserLikeService;

import journeybuddy.spring.domain.UserLike;
import journeybuddy.spring.web.dto.UserLikeDTO.UserLikeResponesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserLikeCommandService {
    UserLike saveLikes(String userEmail, Long postId);
    Page<UserLikeResponesDTO> findMyLike(String userEmail, Pageable pageable);
}
