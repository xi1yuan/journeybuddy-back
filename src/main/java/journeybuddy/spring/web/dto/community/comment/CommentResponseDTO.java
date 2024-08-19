package journeybuddy.spring.web.dto.community.comment;

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
    private Long postId;
    private String createdAt;
}
