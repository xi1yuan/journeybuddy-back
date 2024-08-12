package journeybuddy.spring.service.community.like;

import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserLikeCommandService {
    UserLikeResponesDTO saveLikes(String userEmail, Long postId);
    Page<UserLikeResponesDTO> findMyLike(String userEmail, Pageable pageable);
    void deleteLikes(String userEmail, Long postId);
}
