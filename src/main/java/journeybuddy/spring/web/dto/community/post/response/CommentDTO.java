package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CommentDTO {
    private Long commentId;
    private String content;
    private Long userId;
    private String nickname;
    private String createdAt;

    @Builder
    public CommentDTO(Long commentId, String content, Long userId, String nickname, String createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.userId = userId;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }
}
