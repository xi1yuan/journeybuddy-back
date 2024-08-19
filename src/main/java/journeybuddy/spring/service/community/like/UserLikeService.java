package journeybuddy.spring.service.community.like;

import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserLikeService {
    UserLikeResponesDTO saveLikes(String userEmail, Long postId);
    Page<UserLikeResponesDTO> findMyLike(String userEmail, Pageable pageable);
    void deleteLikes(String userEmail, Long postId);
}
