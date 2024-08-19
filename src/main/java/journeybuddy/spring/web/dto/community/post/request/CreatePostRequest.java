package journeybuddy.spring.web.dto.community.post.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostRequest {
    private String title;
    private String location;
    private String content;
}
