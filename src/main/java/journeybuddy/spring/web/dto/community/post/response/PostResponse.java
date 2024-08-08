package journeybuddy.spring.web.dto.community.post.response;

import journeybuddy.spring.domain.community.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private String location;
    private Long userId;
    private String userName;
    private List<String> imageUrlList;

    @Builder
    public PostResponse(Long postId, String title, String content, String location, Long userId, String userName, List<String> imageUrlList) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.location = location;
        this.userId = userId;
        this.userName = userName;
        this.imageUrlList = imageUrlList;
    }

}
