package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private String location;
    private String writer;
    private String createdAt;
    private List<String> imageUrlList;
    private int likeCount;
    private int commentCount;

    @Builder
    public PostDetailResponse(Long postId, String title, String content, String location, String writer, String createdAt, List<String> imageUrlList, int likeCount, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.writer = writer;
        this.createdAt = createdAt;
        this.imageUrlList = imageUrlList;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
