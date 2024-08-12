package journeybuddy.spring.web.dto.community.post.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDTO {
    private Long id;
    private String url;
}
