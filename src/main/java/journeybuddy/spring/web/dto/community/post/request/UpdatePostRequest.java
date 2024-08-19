package journeybuddy.spring.web.dto.community.post.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePostRequest {
    private String title;
    private String content;
    private String location;
    private List<Long> deletedImageIds;
}
