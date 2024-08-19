package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class CommentWriterDTO {
    private Long userId;
    private String nickname;
}
