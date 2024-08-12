package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostListResponse {
    private Long postId;
    private String title;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private int likeCount;
    private int commentCount;
    private String location;
    private String imageUrl;

    @Builder
    public PostListResponse(Long postId, String title, String content, String writerName, LocalDateTime createdAt, int likeCount, int commentCount, String location, String imageUrl) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.writerName = writerName;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.location = location;
        this.imageUrl = imageUrl;
    }
}
