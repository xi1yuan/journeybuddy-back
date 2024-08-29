package journeybuddy.spring.converter.community;

import journeybuddy.spring.domain.community.UserLike;
import journeybuddy.spring.web.dto.community.like.UserLikeResponesDTO;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Builder
public class UserLikeConverter {

    public static UserLikeResponesDTO toUserLikeResponesDTO(UserLike userLike) {
        return UserLikeResponesDTO.builder()
                .userId(userLike.getUser().getId())
                .id(userLike.getId())
                .likeCount(userLike.getPost().getLikeCount())
                .postResponse(PostResponse.builder()
                        .postId(userLike.getPost().getId())
                        .title(userLike.getPost().getTitle())
                        .content(userLike.getPost().getContent())
                        .location(userLike.getPost().getLocation())
                        .userId(userLike.getPost().getUser().getId())
                        .userName(userLike.getPost().getUser().getNickname())
                        .imageUrlList(userLike.getPost().getImages().stream()
                                .map(journeybuddy.spring.domain.community.Image::getUrl)
                                .collect(Collectors.toList()))
                        .build())
                .build();
    }

}
