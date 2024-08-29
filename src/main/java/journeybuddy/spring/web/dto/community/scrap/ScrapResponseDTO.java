package journeybuddy.spring.web.dto.community.scrap;

import journeybuddy.spring.web.dto.community.post.response.PostResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapResponseDTO {
    private Long id;
    private Long userId;
    private PostResponse postResponse;

}
