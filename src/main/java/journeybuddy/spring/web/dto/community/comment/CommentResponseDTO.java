package journeybuddy.spring.web.dto.community.comment;

import journeybuddy.spring.web.dto.community.post.PostResponseDTO;
import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class CommentResponseDTO {
    private Long id;
    private String comment;
    private Long userId;
    private String createdAt;
    private PostResponse postResponse;
}
