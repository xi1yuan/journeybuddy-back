package journeybuddy.spring.web.dto.community.like;


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
}
