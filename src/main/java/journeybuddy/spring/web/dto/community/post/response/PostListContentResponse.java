package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostListContentResponse {
    private List<PostListResponse> content;
}
