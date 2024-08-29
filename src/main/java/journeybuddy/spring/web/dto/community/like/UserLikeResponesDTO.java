package journeybuddy.spring.web.dto.community.like;


import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLikeResponesDTO {

    private Long id;
    private Long userId;
    private Long postId;
    private int likeCount;
    private PostResponse postResponse;
}
