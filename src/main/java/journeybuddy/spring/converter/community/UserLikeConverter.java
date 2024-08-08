package journeybuddy.spring.converter.community;

import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.web.dto.community.like.UserLikeRequestDTO;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
@Builder
public class UserLikeConverter {


    public static UserLike toUserLike(UserLikeRequestDTO requestDTO) {
        return UserLike.builder()
                .build();
    }


    public  static UserLikeRequestDTO toUserLikeRequestDTO(UserLike userLike) {
        return UserLikeRequestDTO.builder()
        //        .userId(userLike.getUser().getId())
                .postId(userLike.getPost().getId())
                .build();
    }

    public  static UserLikeResponesDTO toUserLikeResponesDTO(UserLike userLike) {
        return UserLikeResponesDTO.builder()
                .userId(userLike.getUser().getId())
                .postId(userLike.getPost().getId())
                .id(userLike.getId())
                .build();
    }
}
